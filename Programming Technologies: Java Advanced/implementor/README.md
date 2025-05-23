# Implementor

## Описание

`Implementor` — это реализация интерфейса `Impler`, которая позволяет генерировать реализацию заданных интерфейсов или абстрактных классов. 

---

## Основные особенности

- Генерация исходного Java-кода для реализации интерфейса или абстрактного класса.
- Поддержка создания `.java` файлов с корректной структурой и методами.
- Обработка исключений, связанных с невозможностью реализации.

---

## API

### Основные методы
- **`implement(Class<?> clazz, Path path)`**: Генерирует реализацию для указанного интерфейса или класса и сохраняет её в указанной директории.
