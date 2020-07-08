# 토비의 스프링 예제코드 따라치기

## 1.1 초난감 DAO

사용자 정보를 JDBC API를 통해 DB에 저장하고 조회할 수 있는 간단한 DAO를 하나 만들어보자.

DAO(Data Access Object)는 DB를 사용해 데이터를 조회하거나 조작하는 기능을 전담하도록 만든 오브젝트를 말한다.

### 1.1.1 User

사용자 정보를 저장할 때는 자바빈 규약을 따르는 오브젝트를 이용하면 편리하다. 

User 오브젝트에 담긴 정보가 실제로 보관될 DB의 테이블을 하나 만들어보자.

테이블 이름은 USER, 프로퍼티는 User 클래스의 프로퍼티와 동일하게 구성한다.

| 필드명   | 타입        | 설정        |
|----------|-------------|-------------|
| Id       | VARCHAR(10) | Primary Key |
| Name     | VARCHAR(20) | Not null    |
| Password | VARCHAR(20) | Not null    |

MySQL DDL Query

```mysql
CREATE TABLE USER (
    id       varchar(10) primary key,
    name     varchar(20) not null,
    password varchar(10) not null
)
```

> 자바빈
> 자바빈(JavaBean)은 원래 비주얼 툴에서 조작 가능한 컴포넌트를 말한다. 하지만 최근엔 다음 두가지 관례를 따라 만들어진 오브젝트를 가리킨다. 간단히 빈이라고 부르기도 한다.
> - 디폴트 생성자: 자바빈은 파라미터가 없는 디폴트 생성자를 갖고 있어야 한다. 툴이나 프레임워크에서 리플렉션을 이용해 오브젝트를 생성하기 때문에 필요하다.
> - 프로퍼티: 자바빈이 노출하는 이름을 가진 속성을 프로퍼티라고 한다. 프로퍼티는 set으로 시작하는 수정자 메소드(setter)와 get으로 시작하는 접근자 메소드(getter)를 이용해 수정 또는 조회할 수 있다.
