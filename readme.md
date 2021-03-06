Хакатон. Команда 2

Терминология:
------------
Доктайп(тип документа) - сущность, включающая в себя:
	- Имя(название типа документа), однозначно идентифицирующее её
	- Url папки с оригиналами документов
	- Mассив полей(их названий, как в заголовке .csv)

Документ(строчка .csv файла) - сущность, включающая в себя следующие поля:
	- Ссылку на доктайп, к которому принадлежит документ. Задается при создании, впоследствие не изменяется
	- Поле checked true/false, определяющее что документ проверен и признан верным(соответствующим оригиналу). Может редактироваться пользователем
	- Поле changed true/false, определяющее были ли поля документа изменены. Изменяется сервером
	- Поле original - название оригинального .pdf файла
	- Массив значений полей - где индекс каждого значения соответствует индексу ключа этого поля в массиве названий полей доктайпа

	Также, при отправке документа с сервера в него добавляется поле checksum - чексумма документа на момент отправки.
	При редактировании документа чексумма отправляется обратно и перед редактированием сверяется с чексуммой документа на сервере
	Таким образом, если между получением документа и отправкой запроса на его редактирование документ был изменён другим пользователем, чексуммы будут различаться,
	и запрос на редактирование будет отклонён

Структура сервера
-----------------

- Основой сервера являются четыре spring rest controller-а, предоставляющих api для получения и редактирования данных
- Подробнее об api - [https://app.swaggerhub.com/apis/96char/Hackathon_Team2_api/v0](https://app.swaggerhub.com/apis/96char/Hackathon_Team2_api/v0)

Всего контроллеров 4:
---------------------
- File controller - загрузка и выгрузака .csv файлов

- Document controller - получение и редактирование документов

- Doctype controller - получение и создание доктайпов

- Staticstics controller - получение статистики


База данных
-----------
	- В разработке использовалась postgreSql версии 13.4

	- В структуре БД используются массивы postgres(https://www.postgresql.org/docs/13/arrays.html)

	- Скрипт для создания БД - hackathon.team2.database.sql
