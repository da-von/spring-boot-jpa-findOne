# Read Me First

## JPA-Bug or not?
A small and easy to set up JPA demo application. It is used to **analyse a possible bug in the JPA implementation**.

## The demo case
The entities `Author` and `Book` are the starting point. Author has a @OneToMany relation with Books: `author.books[]`.

For Author accesses, `AuthorRepository.findOne(long id)` and `AuthorRepository.findOne(long id, EntityGraph entityGraph)` are implemented.

## Platform and components
* `org.springframework.boot:spring-boot-starter-parent:3.0.9`
* `com.cosium.spring.data:spring-data-jpa-entity-graph:3.0.1`
* `org.flywaydb:flyway-core:9.5.1`
* `org.postgresql:postgresql:42.5.4`
* `org.projectlombok:lombok:1.18.28`

## Setup
* [ ] Clone git repository
* [ ] Docker Engine is required
* [ ] Run `docker-compose up`
* [ ] Run test with maven `./mvnw test`
* [ ] Run test class `ch.subsidia.jpademo.JpaDemoApplicationTests` within your development environment


## The demo test

### Test initialization
One Author with three Books

### Successful: `findOne(Specification<T> spec)`
Test for `AuthorRepository.findOne(long id)` is successful. Author is read and access to `author.getBooks()` returns `LazyInitializationException` because no EntityGraph is present..

### Fail: `findOne(Specification<T> spec, EntityGraph entityGraph)`

Test for `AuthorRepository.findOne(long id, EntityGraph entityGraph)` delegates the data acces to `EntityGraphJpaSpecificationExecutor.findOne(Specification<T> spec, EntityGraph entityGraph)` 
and fails because `author.getBooks()` contains **only 2 Books, but 3 are expected**.

## Log
The failing test log this query:
```sql
select a1_0.id,b1_0.author_id,b1_0.id,b1_0.genre,b1_0.isbn,b1_0.title,a1_0.name 
  from author a1_0 
      left join book b1_0 on a1_0.id=b1_0.author_id 
  where a1_0.id=? 
  fetch first ? rows only
```
Is `fetch first ? rows only` correct here? Or a bug?


## Summary statement
Regardless of whether an EntityGraph is specified in findOne or not, 
the SQL query is generated with `fetch first ? rows only`. If an `outer` join is generated on the basis of an EntityGraph, then data is truncated and not delivered completely. 


