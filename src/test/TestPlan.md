#Какие кейсы надо покрыть:

## Модуль парсинга
- Нормальный случай, когда все валидно
- Невалидный ввод
  - Файла не существует
  - Файл пустой
  - Файл содержит какую-то фигню
  - Файл не содержит классов (только интерфейсы, енумы?)

## Модуль фильтрации
- Без фильтров
- Фильтр по имени
  - Класс существует
  - Класса не существует
  - Есть классы ClassA.ClassB и ClassA.ClassBBBB, т.е. у нас есть подстроки
- Черный и белый список имен
- Фильтр по модификаторам доступа

## Модуль анализа
- Простой случай
- Метод является реализацией другого интерфейса
- Метод без параметров
- Метод без возвращаемого значения
- Метод с переменным числом аргументов
- Параметр - массив

## Модуль генерации кода
- Простой случай
- Пустой интерфейс
- Переименование интерфейса
- Преобразование имен
- Преобразование типов
