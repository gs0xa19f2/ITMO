package info.kgeorgiy.ja.gusev.arrayset;

import java.util.*;

public class ArraySet<E> implements NavigableSet<E> {
    private final List<E> elements;
    private final Comparator<? super E> comparator;

    
    public ArraySet(Collection<? extends E> collection, Comparator<? super E> comparator) {
        TreeSet<E> set = new TreeSet<>(comparator);
        set.addAll(collection);
        this.elements = Collections.unmodifiableList(new ArrayList<>(set));
        this.comparator = comparator;
    }

    public ArraySet(List<E> list, Comparator<? super E> comparator) {
        this.elements = Collections.unmodifiableList(list);
        this.comparator = comparator;
    }

    
    @Override
    public E lower(E e) {
        int index = Collections.binarySearch(elements, e, comparator);
        if (index < 0) {
            index = -index - 2;
        } else {
            index--;
        }
        return index >= 0 ? elements.get(index) : null;
    }

    
    @Override
    public E floor(E e) {
        int index = Collections.binarySearch(elements, e, comparator);
        if (index < 0) {
            index = -index - 2;
        }
        return index >= 0 ? elements.get(index) : null;
    }

    
    @Override
    public E ceiling(E e) {
        int index = Collections.binarySearch(elements, e, comparator);
        if (index < 0) {
            index = -index - 1;
        }
        return index < elements.size() ? elements.get(index) : null;
    }

    
    @Override
    public E higher(E e) {
        int index = Collections.binarySearch(elements, e, comparator);
        if (index < 0) {
            index = -index - 1;
        } else {
            index++;
        }
        return index < elements.size() ? elements.get(index) : null;
    }

    
    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException();
    }

    
    @Override
    public E pollLast() {
        throw new UnsupportedOperationException();
    }

    
    @Override
    public NavigableSet<E> descendingSet() {
        return new ArraySet<>(new ReversedList<>(elements), Collections.reverseOrder(comparator));
    }

    
    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    
    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        int toIndex = findIndex(toElement);
        if (inclusive && toIndex < elements.size() && compare(elements.get(toIndex), toElement) == 0) {
            toIndex++;
        }
        return new ArraySet<>(elements.subList(0, toIndex), comparator);
    }

    
    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        int fromIndex = findIndex(fromElement);
        if (!inclusive && fromIndex < elements.size() && compare(elements.get(fromIndex), fromElement) == 0) {
            fromIndex++;
        }
        return fromIndex >= elements.size() ? new ArraySet<>(Collections.emptyList(), comparator)
                : new ArraySet<>(elements.subList(fromIndex, elements.size()), comparator);
    }

    
    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        if (compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("fromElement > toElement");
        }
        int fromIndex = findIndex(fromElement);
        int toIndex = findIndex(toElement);
        if (!fromInclusive && fromIndex < elements.size() && compare(elements.get(fromIndex), fromElement) == 0) {
            fromIndex++;
        }
        if (toInclusive && toIndex < elements.size() && compare(elements.get(toIndex), toElement) == 0) {
            toIndex++;
        }
        
        if (fromIndex > toIndex) {
            toIndex = fromIndex;
        }
        return new ArraySet<>(elements.subList(fromIndex, toIndex), comparator);
    }

    

    
    @Override
    public SortedSet<E> headSet(E toElement) {
        int index = findIndex(toElement);
        return new ArraySet<>(elements.subList(0, index), comparator);
    }

    
    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        if (compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("fromKey > toKey");
        }
        int fromIndex = findIndex(fromElement);
        int toIndex = findIndex(toElement);
        return new ArraySet<>(elements.subList(fromIndex, toIndex), comparator);
    }

    
    @Override
    public SortedSet<E> tailSet(E fromElement) {
        int index = findIndex(fromElement);
        return new ArraySet<>(elements.subList(index, elements.size()), comparator);
    }

    
    private int findIndex(E element) {
        int index = Collections.binarySearch(elements, element, comparator);
        return index < 0 ? -(index + 1) : index;
    }

    
    private int compare(E e1, E e2) {
        if (comparator == null) {
            return ((Comparable<? super E>) e1).compareTo(e2);
        }
        return comparator.compare(e1, e2);
    }

    
    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    
    @Override
    public Object[] toArray() {
        return elements.toArray();
    }

    
    @Override
    public <T> T[] toArray(T[] a) {
        return elements.toArray(a);
    }

    
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object item : c) {
            if (!contains(item)) {
                return false;
            }
        }
        return true;
    }

    
    public ArraySet() {
        this.elements = Collections.emptyList();
        this.comparator = null;
    }

    
    public ArraySet(Collection<? extends E> collection) {
        this(collection, null);
    }

    
    @Override
    public int size() {
        return elements.size();
    }

    
    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(elements, (E) o, comparator) >= 0;
    }

    
    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    
    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    
    @Override
    public E first() {
        if (elements.isEmpty()) {
            throw new NoSuchElementException();
        }
        return elements.get(0);
    }

    
    @Override
    public E last() {
        if (elements.isEmpty()) {
            throw new NoSuchElementException();
        }
        return elements.get(elements.size() - 1);
    }

    
    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    
    private static class ReversedList<E> extends AbstractList<E> {
        private final List<E> original;

        public ReversedList(List<E> original) {
            this.original = original;
        }

        @Override
        public E get(int index) {
            return original.get(original.size() - 1 - index);
        }

        @Override
        public int size() {
            return original.size();
        }
    }
}

