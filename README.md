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
CREATE DATABASE springbook;

CREATE USER 'spring'@'%' IDENTIFIED BY 'book';

GRANT ALL PRIVILEGES ON springbook.* TO 'spring'@'%';

FLUSH PRIVILEGES;

USE springbook;

CREATE TABLE USER (
    id       varchar(10) primary key,
    name     varchar(20) not null,
    password varchar(10) not null
);
```

> 자바빈
> 자바빈(JavaBean)은 원래 비주얼 툴에서 조작 가능한 컴포넌트를 말한다. 하지만 최근엔 다음 두가지 관례를 따라 만들어진 오브젝트를 가리킨다. 간단히 빈이라고 부르기도 한다.
> - 디폴트 생성자: 자바빈은 파라미터가 없는 디폴트 생성자를 갖고 있어야 한다. 툴이나 프레임워크에서 리플렉션을 이용해 오브젝트를 생성하기 때문에 필요하다.
> - 프로퍼티: 자바빈이 노출하는 이름을 가진 속성을 프로퍼티라고 한다. 프로퍼티는 set으로 시작하는 수정자 메소드(setter)와 get으로 시작하는 접근자 메소드(getter)를 이용해 수정 또는 조회할 수 있다.

UserDao는 사용자 정보의 등록, 수정, 삭제 및 각종 조회 기능을 만들어야 하지만, 일단 두개의 메소드를 먼저 만든다.

JDBC를 이용하는 작업의 일반적인 순서는 다음과 같다.

- DB 연결을 위한 Connection을 가져온다.
- SQL을 담은 Statement(또는 PreparedStatement)를 만든다.
- 만들어진 Statement를 실행한다.
- 조회의 경우 SQL 쿼리의 실행 결과를 ResultSet으로 받아서 정보를 저장할 오브젝트(여기서는 `User`)로 옮겨준다.
- 작업 중에 생성된 Connection, Statement, ResultSet 같은 리소스는 작업을 마친 후 반드시 닫아준다.
   - java7에서 추가된 try with resource를 활용하면 더 낫겠다.
- JDBC API가 만들어내는 예외를 잡아서 직접 처리하거나, 메소드에 throws를 선언해서 예외가 발생하면 모두 메소드 밖으로 던지게 한다.

일단 예외는 모두 메소드 밖으로 던지도록 하였다.

## 1.2 DAO의 분리

### 1.2.1 관심사의 분리

### 1.2.2 커넥션 만들기의 추출

UserDao의 구현된 메소드를 자세히 들여다보면 `add()` 메소드 하나에서만 적어도 세 가지 관심사항을 발견할 수 있다.

**UserDao의 관심사항**

- 첫째는 DB와 연결을 위한 커넥션을 어떻게 가져올까라는 관심이다.(어떤 DB, 어떤 드라이버, 어떤 로그인 정보, 커넥션을 생성하는 방법)
- 사용자 등록을 위해 DB에 보낼 SQL 문장을 담은 Statement를 만들고 실행하는 것.(파라미터 바인딩, 어떤 SQL을 사용할지)
- 작업이 끝나면 사용한 리소스인 Statement와 Connection 오브젝트를 닫아줘서 소중한 공유 리소스를 시스템에 돌려주는 것

UserDao에 대한 코드는 예외상황에 대한 처리가 전혀 없다. 예외상황이 발생하더라도 공유 리소스를 반환할 수 있도록 처리를 해줘야 한다.

첫 번째 관심사에 집중해보면, 중복된 코드가 `get()`에도 동일하게 존재한다. 바로 이렇게 하나의 관심사가 중복이 되어있고, 여기저기 흩어져 있어서 다른 관심의 대상과 얽혀있으면, 변경이 일어날 때 엄청난 고통을 일으키는 원인이 된다. 스파게티 코드가 된다는 뜻이다.

**중복 코드의 메소드 추출**

가장 먼저 할 일은 커넥션을 가져오는 중복된 코드를 분리하는 것이다. 중복된 DB연결 코드를 `getConnection()`이라는 이름의 독립적인 메소드로 만들어둔다.

이렇게 하면 관심의 종류에 따라 코드를 구분해놓았기 때문에 한 가지 관심에 대한 변경이 일어날 경우 그 관심이 집중되는 부분의 코드만 수정하면 된다. 관심이 다른 코드가 있는 메소드에는 영향을 주지도 않을뿐더러, 관심 내용이 독립적으로 존재하므로 수정도 간단해졌다.

**변경사항에 대한 검증: 리팩토링과 테스트**

코드를 수정한 후에는 기능에 문제가 없다는 것이 보장되지 않는다. 다시 검증이 필요하다. `main()`메소드를 다시 사용하면 될까? 이것의 단점은 여러번 실행하면 두 번째부터는 무조건 예외가 발생한다는 점이다. 따라서 테스트를 다시하기 위해서는 사용자 정보를 모두 삭제해줘야 한다. 수정한 코드의 검증은 다시 `main()`메소드를 실행해서 처음과 같은 결과가 화면에 출력되는지를 확인해보면 된다.

방금 한 작업은 UserDao의 기능에는 아무런 변화를 주지 않았지만, 코드의 구조를 변경한 것이다. 훨씬 깔끔해졌고, 미래의 변화에 손쉽게 대응할 수 있는 코드가 됐다. 이런 작업을 **리팩토링(Refactoring)** 이라고 한다. 또한 위에서 한 작업을 **메소드 추출(extract method)기법**이라고 부른다.

리팩토링은 객체지향 개발자라면 반드시 익혀야하는 기법이다.

### 1.2.3 DB 커넥션 만들기의 독립

소스를 공개하지 않고, 사용자가 스스로 원하는 DB 커넥션 생성 방식을 적용해가면서 UserDao를 사용하도록 하려면 어떻게 해야할까?

**상속을 통한 확장**

UserDao를 한 단계 더 분리한다. 메소드의 구현 코드를 제거하고 `getConnection()`을 추상 메소드로 만들어놓는다.  
그러면 원하는 DB Connection 생성방식에 맞게 원하는 방식으로 확장한 후에 UserDao의 기능과 함께 사용할 수 있다.

기존에는 같은 클래스에 다른 메소드로 분리됐던 DB 커넥션 연결이라는 관심을 이번에는 상속을 통해 서브클래스로 분리해버리는 것이다.

수정한 코드를 잘 살펴보자. DAO의 핵심 기능인 어떻게 데이터를 등록하고 가져올 것인가(SQL 작성, 파라미터 바인딩, 쿼리 실행, 검색정보 전달)라는 관심을 담당하는 UserDao와, DB 연결 방법은 어떻게 할 것인가라는 관심을 담고 있는 NUserDao, DUserDao가 클래스 레벨로 구분이 되고 있다.

이제 UserDao는 단순히 변경이 용이하다라는 수준을 넘어서 손쉽게 확장된다라고 말할 수도 있게 됐다. 새로운 DB 연결 방법을 적용해야 할 때는 UserDao를 상속을 통해 확장해주기만 하면 된다.

이렇게 슈퍼클래스에 기본적인 로직의 흐름(커넥션 가져오기, SQL 생성, 실행, 반환)을 만들고, 그 기능의 일부를 추상 메소드나 오버라이딩이 가능한 protected 메소드 등으로 만든뒤 서브클래스에서 이런 메소드를 필요에 맞게 구현해서 사용하도록 하는 방법을 디자인 패턴에서 **템플릿 메소드 패턴(Template method pattern)** 이라고 한다.  
템플릿 메소드 패턴은 스프링에서 애용되는 디자인 패턴이다.

UserDao의 서브클래스의 `getConnection()` 메소드는 어떤 Connection 클래스의 오브젝트를 어떻게 생성할 것인지를 결정하는 방법이라고도 볼 수 있다. 이렇게 서브클래스에서 구체적인 오브젝트 생성 방법을 결정하게 하는 것을 **팩토리 메소드 패턴(Factory method pattern)** 이라고 부르기도 한다.  
UserDao는 그저 Connection에 정의된 기능을 사용하는 데에만 관심이 있고, NUserDao나 DUserDao에서는 어떤 식으로 Connection 기능을 제공하는지에 관심을 두고 있는 것이다. 또 어떤 방법으로 Connection 오브젝트를 만들어내는지도 관심 사항이다.  
UserDao는 Connection 오브젝트가 만들어지는 방법과 내부 동작방식에는 상관없이 자신이 필요한 기능을 Connection 인터페이스를 통해 사용하기만 할 뿐이다.

디자인 패턴 용어에 당황하지 말자, 디자인 패턴에 익숙하지 않거나 패턴의 종류를 잘 모르더라도 괜찮다. 중요한 건 상속구조를 통해 성격이 다른 관심사항을 분리한 코드를 만들어내고, 서로 영향을 덜 주도록 했는지를 이해하는 것이다.

결국 위 방법은 상속을 사용했다는 단점이 있다. 상속 자체는 간단해 보이고 사용하기도 편리하게 느껴지지 만 사실 많은 한계점이 있다.  
만약 이미 UserDao가 다른 목적을 위해 상속을 사용하고 있다면 어쩔 것인가? 자바는 클래스의 다중상속을 허용하지 않는다. 단지, 커넥션 객체를 가져오는 방법을 분리하기 위해 상속구조로 만들어버리면, 후에 다른 목적으로 UserDao에 상속을 적용하기 힘들다.

또 다른 문제는 상속을 통한 상하위 클래스의 관계는 생각보다 밀접하다는 점이다. 상속을 통해 관심이 다른 기능을 분리하고, 필요에 따라 다양한 변신이 가능하도록 확장성도 줬지만 여전히 상속관계는 두 가지 다른 관심사에 대해 긴밀한 결합을 허용한다. 서브클래스는 슈퍼클래스의 기능을 직접 사용할 수 있다. 그래서 슈퍼클래스의 내부의 변경이 있을 때, 모든 서브클래스를 함께 수정하거나 다시 개발해야 할 수도 있다. 그런 변화에 따른 불편을 주지 않기 위해 슈퍼클래스가 더 이상 변화하지 않도록 제약을 가해야 할지도 모른다.

확장된 기능인 DB 커넥션을 연결하는 코드를 다른 DAO 클래스에 적용할 수 없다는 것도 큰 단점이다. 만약 UserDao 외의 DAO 클래스들이 계속 만들어진다면 그때는 상속을 통해서 만들어진 `getConnection()`의 구현 코드가 매 DAO 클래스마다 중복해서 나타나는 심각한 문제가 발생할 것이다.
