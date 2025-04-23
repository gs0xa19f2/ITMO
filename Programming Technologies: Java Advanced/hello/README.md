# HelloUDP

## Описание

Проект `HelloUDP` представляет клиент и сервер, работающие по протоколу UDP. Реализация включает как блокирующие, так и неблокирующие версии.

---

## Основные модули

- **`HelloUDPClient`**: Отправляет запросы на сервер и обрабатывает ответы.
- **`HelloUDPServer`**: Принимает запросы от клиентов и отправляет ответы.
- **`HelloUDPNonblockingClient`**: Асинхронная версия клиента.
- **`HelloUDPNonblockingServer`**: Асинхронная версия сервера.

---

## API

### Основные методы
- **`HelloUDPClient.run(String host, int port, String prefix, int threads, int requests)`**: Запускает клиента с указанными параметрами.
- **`HelloUDPServer.start(int port, int threads)`**: Запускает сервер на указанном порту с заданным количеством потоков.
