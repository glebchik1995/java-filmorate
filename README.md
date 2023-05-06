# java-filmorate

### Модель базы данных приложения Filmorate 

---
![ER-диаграмма](../../Downloads/Untitled.png)
### Примеры запросов для основных операций

---

<details>
  <summary>Получить таблицу со всеми данными пользователя с id=1</summary>

```sql
    SELECT *
    FROM users
    WHERE user_id = 1;
```

</details>  
<details>
  <summary>Получить таблицу со всеми данными фильма с id=1</summary>

```sql
    SELECT *
    FROM films
    WHERE film_id = 1;
```

</details>  
