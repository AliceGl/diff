# Курс основ программирования на МКН СПбГУ
## Проект 1: утилита diff

[Постановка задачи](./TASK.md)

### Описание работы программы

Утилита diff реализована в файле main.kt, аргументы программа принимает
как из командной строки, так и из стандартного ввода, причем первую часть 
аргументов можно передать в качестве параметров командной строки, а
остальные ввести после запуска. Причем конец ввода требуется обозначить
в стандартном потоке ввода.

Опции утилиты передаются первыми аргументами и начинаются с символа -,
после которого идут символы, обозначающие опции. Следующие два
аргумента задают пути к сравниваемым файлам. Последний аргумент задаёт
путь к файлу для вывода результата работы утилиты, если аргумент
отсутствует, то результат будет направлен в стандартный поток вывода.

### Опции утилиты

#### Опции, задающие формат вывода

Опция | Формат
----- | ------
-e | EditScript
-c | Copied context
-u | Unified context
-y | Вывод в два столбца

Может быть применена лишь одна из этих опций, если же ни одна из опций
не применена, то результат выведется в стандартном формате

#### Другие опции

Опция | Значение
----- | ------
-s | Уведомляет, если файлы идентичны
-i | Игнорирует различия в регистре

### Примеры вывода

Рассмотрим файлы original и new:

#### *Original:*
<pre >
 1| This part of the
 2| document has stayed the
 3| same from version to
 4| version.  It shouldn't
 5| be shown if it doesn't
 6| change.  Otherwise, that
 7| would not be helping to
 8| compress the size of the
 9| changes.
10| 
11| This paragraph contains
12| text that is outdated.
13| It will be deleted in the
14| near future.
15|
16| It is important to spell
17| check this DoCuMeNt. On
18| the other hand, a
19| misspelled word isn't
20| the end of the world.
21| Nothing in the rest of
22| this paragraph needs to
23| be changed. Things can
24| be added after it.
</pre>

#### *New:*

<pre>
 1| This is an important
 2| notice! It should
 3| therefore be located at
 4| the beginning of this
 5| document!
 6|
 7| This part of the
 8| document has stayed the
 9| same from version to
10| version.  It shouldn't
11| be shown if it doesn't
12| change.  Otherwise, that
13| would not be helping to
14| compress the size of the
15| changes.
16|
17| It is important to spell
18| check this document. On
19| the other hand, a
20| misspelled word isn't
21| the end of the world.
22| Nothing in the rest of
23| this paragraph needs to
24| be changed. Things can
25| be added after it.
26| 
27| This paragraph contains
28| important new additions
29| to this document.
</pre>

Вывод программы при запуске со следующими аргументами:

> original new

<pre>
0a1,6
> This is an important
> notice! It should
> therefore be located at
> the beginning of this
> document!
> 
10,14d15
< 
< This paragraph contains
< text that is outdated.
< It will be deleted in the
< near future.
---
17c18
< check this DoCuMeNt. On
---
> check this document. On
24a26,29
> 
> This paragraph contains
> important new additions
> to this document.
</pre>

> -e original new

<pre>
24a

This paragraph contains
important new additions
to this document.
.
17c
check this document. On
.
10,14d
0a
This is an important
notice! It should
therefore be located at
the beginning of this
document!

.
</pre>

> -c original new

<pre>
*** original
--- new
***************
*** 1,3 ****
--- 1,9 ----
+ This is an important
+ notice! It should
+ therefore be located at
+ the beginning of this
+ document!
+ 
  This part of the
  document has stayed the
  same from version to
***************
*** 7,20 ****
  would not be helping to
  compress the size of the
  changes.
- 
- This paragraph contains
- text that is outdated.
- It will be deleted in the
- near future.
  
  It is important to spell
! check this DoCuMeNt. On
  the other hand, a
  misspelled word isn't
  the end of the world.
--- 13,21 ----
  would not be helping to
  compress the size of the
  changes.
  
  It is important to spell
! check this document. On
  the other hand, a
  misspelled word isn't
  the end of the world.
***************
*** 22,24 ****
--- 23,29 ----
  this paragraph needs to
  be changed. Things can
  be added after it.
+ 
+ This paragraph contains
+ important new additions
+ to this document.
</pre>

> -u original new

<pre>
--- original
+++ new
@@ -1,3 +1,9 @@
+This is an important
+notice! It should
+therefore be located at
+the beginning of this
+document!
+
 This part of the
 document has stayed the
 same from version to
@@ -7,14 +13,9 @@
 would not be helping to
 compress the size of the
 changes.
-
-This paragraph contains
-text that is outdated.
-It will be deleted in the
-near future.
 
 It is important to spell
-check this DoCuMeNt. On
+check this document. On
 the other hand, a
 misspelled word isn't
 the end of the world.
@@ -22,3 +23,7 @@
 this paragraph needs to
 be changed. Things can
 be added after it.
+
+This paragraph contains
+important new additions
+to this document.
</pre>

> -y original new

<pre>
                                                               > This is an important
                                                               > notice! It should
                                                               > therefore be located at
                                                               > the beginning of this
                                                               > document!
                                                               > 
This part of the                                                 This part of the
document has stayed the                                          document has stayed the
same from version to                                             same from version to
version.  It shouldn't                                           version.  It shouldn't
be shown if it doesn't                                           be shown if it doesn't
change.  Otherwise, that                                         change.  Otherwise, that
would not be helping to                                          would not be helping to
compress the size of the                                         compress the size of the
changes.                                                         changes.
                                                               <
This paragraph contains                                        <
text that is outdated.                                         <
It will be deleted in the                                      <
near future.                                                   <
                                                                 
It is important to spell                                         It is important to spell
check this DoCuMeNt. On                                        | check this document. On
the other hand, a                                                the other hand, a
misspelled word isn't                                            misspelled word isn't
the end of the world.                                            the end of the world.
Nothing in the rest of                                           Nothing in the rest of
this paragraph needs to                                          this paragraph needs to
be changed. Things can                                           be changed. Things can
be added after it.                                               be added after it.
                                                               > 
                                                               > This paragraph contains
                                                               > important new additions
                                                               > to this document.
</pre>

> -s original original

<pre>
Files original and original are identical
</pre>

> -iu original new

<pre>
--- original
+++ new
@@ -1,3 +1,9 @@
+This is an important
+notice! It should
+therefore be located at
+the beginning of this
+document!
+
 This part of the
 document has stayed the
 same from version to
@@ -7,11 +13,6 @@
 would not be helping to
 compress the size of the
 changes.
-
-This paragraph contains
-text that is outdated.
-It will be deleted in the
-near future.
 
 It is important to spell
 check this DoCuMeNt. On
@@ -22,3 +23,7 @@
 this paragraph needs to
 be changed. Things can
 be added after it.
+
+This paragraph contains
+important new additions
+to this document.
</pre>

> -i -y original new

<pre>
                                                               > This is an important
                                                               > notice! It should
                                                               > therefore be located at
                                                               > the beginning of this
                                                               > document!
                                                               > 
This part of the                                                 This part of the
document has stayed the                                          document has stayed the
same from version to                                             same from version to
version.  It shouldn't                                           version.  It shouldn't
be shown if it doesn't                                           be shown if it doesn't
change.  Otherwise, that                                         change.  Otherwise, that
would not be helping to                                          would not be helping to
compress the size of the                                         compress the size of the
changes.                                                         changes.
                                                               <
This paragraph contains                                        <
text that is outdated.                                         <
It will be deleted in the                                      <
near future.                                                   <
                                                                 
It is important to spell                                         It is important to spell
check this DoCuMeNt. On                                          check this document. On
the other hand, a                                                the other hand, a
misspelled word isn't                                            misspelled word isn't
the end of the world.                                            the end of the world.
Nothing in the rest of                                           Nothing in the rest of
this paragraph needs to                                          this paragraph needs to
be changed. Things can                                           be changed. Things can
be added after it.                                               be added after it.
                                                               > 
                                                               > This paragraph contains
                                                               > important new additions
                                                               > to this document.
</pre>