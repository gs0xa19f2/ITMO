package info.kgeorgiy.ja.gusev.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;

public class WebCrawler implements NewCrawler {
    
    private final Downloader downloader;
    
    private final ExecutorService downloadExecutor;
    
    private final ExecutorService extractExecutor;
    
    private final ConcurrentMap<String, Semaphore> hostSemaphores;
    
    private final int maxConnectionsPerHost;
    
    private final int timeout;

    
    public WebCrawler(final Downloader downloader, final int downloaders, final int extractors, final int perHost, final int timeout) {
        this.downloader = downloader;
        this.downloadExecutor = Executors.newFixedThreadPool(downloaders);
        this.extractExecutor = Executors.newFixedThreadPool(extractors);
        this.hostSemaphores = new ConcurrentHashMap<>();
        this.maxConnectionsPerHost = perHost;
        this.timeout = timeout;
    }

    
    public WebCrawler(final Downloader downloader, final int downloaders, final int extractors, final int perHost) {
        this(downloader, downloaders, extractors, perHost, 10); 
    }

    
    @Override
    public Result download(final String url, final int depth) {
        return download(url, depth, Set.of());
    }

    
    @Override
    public Result download(final String url, final int depth, final Set<String> excludes) {
        final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();  
        final Map<String, IOException> downloadErrors = new ConcurrentHashMap<>();  
        final Phaser taskCoordinator = new Phaser(1);  
        final BlockingQueue<String> pendingUrls = new LinkedBlockingQueue<>();  

        pendingUrls.add(url);  

        
        for (int currentDepth = 0; currentDepth < depth; currentDepth++) {
            final List<String> currentBatchUrls = new ArrayList<>(pendingUrls);
            pendingUrls.clear();  
            final int effectiveDepth = depth - currentDepth;  

            currentBatchUrls.stream()
                .filter(link -> isValidLink(link, visitedUrls, excludes))  
                .forEach(link -> initiateDownload(link, effectiveDepth, pendingUrls, excludes, downloadErrors, taskCoordinator));

            taskCoordinator.arriveAndAwaitAdvance();  
        }

        visitedUrls.removeAll(downloadErrors.keySet());  
        return new Result(new ArrayList<>(visitedUrls), downloadErrors);
    }

    
    private boolean isValidLink(final String url, final Set<String> visited, final Set<String> excludes) {
        return !isExcluded(url, excludes) && visited.add(url);
    }

    
    private boolean isExcluded(String url, Set<String> excludes) {
        return excludes.stream().anyMatch(url::contains);
    }

    
    private void initiateDownload(final String url, final int remainingDepth, final BlockingQueue<String> nextUrlsQueue,
                                  final Set<String> excludes, final Map<String, IOException> errors, final Phaser phaser) {
        try {
            final String host = URLUtils.getHost(url);  
            final Semaphore semaphore = hostSemaphores.computeIfAbsent(host, h -> new Semaphore(maxConnectionsPerHost));  

            phaser.register(); 
            downloadExecutor.submit(() -> {
                try {
                    semaphore.acquire();  
                    try {
                        final Document document = downloader.download(url);  
                        if (remainingDepth > 1) {
                            initiateExtract(document, nextUrlsQueue, excludes, phaser);  
                        }
                    } catch (IOException e) {
                        errors.put(url, e);  
                    } finally {
                        semaphore.release();  
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();  
                } finally {
                    phaser.arriveAndDeregister();  
                }
            });
        } catch (MalformedURLException e) {
            errors.put(url, new IOException("Неверный URL: " + url, e));  
        }
    }

    
    private void initiateExtract(final Document document, final BlockingQueue<String> nextUrlsQueue,
                                 final Set<String> excludes, final Phaser phaser) {
        phaser.register();  
        extractExecutor.submit(() -> {
            try {
                document.extractLinks().stream()  
                    .filter(link -> !isExcluded(link, excludes))  
                    .forEach(nextUrlsQueue::offer);  
            } catch (IOException ignored) {
            } finally {
                phaser.arriveAndDeregister();  
            }
        });
    }

    
    @Override
    public void close() {
        shutdownExecutor(downloadExecutor); 
        shutdownExecutor(extractExecutor);  
    }


    private void shutdownExecutor(final ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(timeout, TimeUnit.SECONDS)) {
                executor.shutdownNow();  
            }
        } catch (InterruptedException e) {
            executor.shutdownNow(); 
            Thread.currentThread().interrupt();  
        }
    }

    public static void main(final String[] args) {
        if (args.length < 1 || args.length > 5) {
            System.err.println("Usage: WebCrawler url [depth [downloads [extractors [perHost]]]]");
            return;
        }

        final String url = args[0];
        final int depth = args.length > 1 ? Integer.parseInt(args[1]) : 1;
        final int downloaders = args.length > 2 ? Integer.parseInt(args[2]) : 16;
        final int extractors = args.length > 3 ? Integer.parseInt(args[3]) : 16;
        final int perHost = args.length > 4 ? Integer.parseInt(args[4]) : 16;

        try (WebCrawler crawler = new WebCrawler(new CachingDownloader(1.0), downloaders, extractors, perHost)) {
            final Result result = crawler.download(url, depth);  
            System.out.println("Downloaded: " + result.getDownloaded().size() + " pages");
            System.out.println("Errors: " + result.getErrors().size());
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());  
        }
    }
}

