
# Things can be improved IMHO
But I'm not sure how to do it yet...

----
1. Learn Polymorphism and Abstraction, so I can use List, Map etc.


2. `model.ru.yandex.practicum.tasktracker.Epic.class` :  until Polymorphism will be used setStatus for model.ru.yandex.practicum.tasktracker.Epic has been  exposured, later we will have `BaseTask.class`
   with no setStatus available for derived class. If they will need it they will add it.
```
   public void updateStatus() {
   this.setStatus(calculateStatus());
   }
```
3. Learn how to use Generic, to the number of methods in service.ru.yandex.practicum.tasktracker.InMemoryTaskManager will be reduced.