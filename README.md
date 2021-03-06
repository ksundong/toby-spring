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

결국 위 방법은 상속을 사용했다는 단점이 있다. 상속 자체는 간단해 보이고 사용하기도 편리하게 느껴지지만 사실 많은 한계점이 있다.  
만약 이미 UserDao가 다른 목적을 위해 상속을 사용하고 있다면 어쩔 것인가? 자바는 클래스의 다중상속을 허용하지 않는다. 단지, 커넥션 객체를 가져오는 방법을 분리하기 위해 상속구조로 만들어버리면, 후에 다른 목적으로 UserDao에 상속을 적용하기 힘들다.

또 다른 문제는 상속을 통한 상하위 클래스의 관계는 생각보다 밀접하다는 점이다. 상속을 통해 관심이 다른 기능을 분리하고, 필요에 따라 다양한 변신이 가능하도록 확장성도 줬지만 여전히 상속관계는 두 가지 다른 관심사에 대해 긴밀한 결합을 허용한다. 서브클래스는 슈퍼클래스의 기능을 직접 사용할 수 있다. 그래서 슈퍼클래스의 내부의 변경이 있을 때, 모든 서브클래스를 함께 수정하거나 다시 개발해야 할 수도 있다. 그런 변화에 따른 불편을 주지 않기 위해 슈퍼클래스가 더 이상 변화하지 않도록 제약을 가해야 할지도 모른다.

확장된 기능인 DB 커넥션을 연결하는 코드를 다른 DAO 클래스에 적용할 수 없다는 것도 큰 단점이다. 만약 UserDao 외의 DAO 클래스들이 계속 만들어진다면 그때는 상속을 통해서 만들어진 `getConnection()`의 구현 코드가 매 DAO 클래스마다 중복해서 나타나는 심각한 문제가 발생할 것이다.

## 1.3 DAO의 확장

지금까지 **데이터 엑세스 로직을 어떻게 만들 것인가**와 **DB 연결을 어떤 방법으로 할 것인가**라는 두 개의 관심을 상하위 클래스로 분리시켰다.

이 두 개의 관심은 변화의 성격이 다르다. 변화의 성격이 다르다는 건 '변화의 이유와 시기, 주기 등이 다르다'는 뜻이다.

`UserDao`는 JDBC API를 사용할 것인가, DB 전용 API를 사용할 것인가와 같은 관심을 가진 코드를 모아둔 것이다. 따라서 이런 관심사가 바뀌면 그 때 변경이 일어난다. 하지만 이때도 DB 연결 방법이 그대로라면 DB 연결 확장 기능을 담은 `NUserDao`나 `DUserDao`의 코드는 변하지 않는다.

반대로, 사용자 정보를 저장하고 가져오는 방법에 대한 관심은 바뀌지 않지만 DB 연결 방식이나 DB 커넥션을 가져오는 방법이 바뀌면, 그때는 `UserDao` 코드는 그대로인 채로 `NUserDao`나 `DUserDao`의 코드만 바뀐다.

추상 클래스를 만들고 이를 상속한 서브클래스에서 변화가 필요한 부분을 바꿔서 쓸 수 있게 만든 이유는 바로 이렇게 변화의 성격이 다른 것을 분리해서, 서로 영향을 주지 않은 채로 각각 필요한 시점에 독립적으로 변경할 수 있게 하기 위해서다. 하지만, 단점이 많은 상속을 사용했다는 사실이 불편하게 느껴진다.

### 1.3.1 클래스의 분리

두 개의 관심사를 본격적으로 독립시키면서 동시에 손쉽게 확장할 수 있는 방법을 알아보자.

#### 현재까지 해왔던 관심사를 분리하는 작업

1. [독립된 메소드를 만들어서 분리](#122-커넥션-만들기의-추출)
2. [상하위 클래스로 분리](#123-DB-커넥션-만들기의-독립)

이번에는 아예 상속관계도 아닌 완전히 독립적인 클래스로 만들어보겠다.

방법은 DB 커넥션과 관련된 부분을 서브클래스가 아니라, 아예 별도의 클래스에 담는다. 그리고 이렇게 만든 클래스를 `UserDao`가 이용하게 하면 된다.

`SimpleConnectionMaker`라는 새로운 클래스를 만들고, DB 커넥션 생성 기능을 그 안에 넣는다. 그리고 `UserDao`는 `SimpleConnectionMaker`클래스의 오브젝트를 만들어서 사용하면 된다. 이 오브젝트는 한 번 만들어서 저장해두고 계속 사용하는 편이 낫다.

기존 코드를 많이 수정했지만, 기능에 변화를 주지 않았고, 내부 설계를 변경해서 더 나은 코드로 개선했다. 하지만, 기능의 변화가 없다는건 리팩토링 작업의 전제기도 하지만, 사실은 검증 내용이기도 하다.(테스트 코드가 리팩토링 작업에 필요한 이유)

하지만, 이 코드에도 문제가 있다. 바로 N 사와 D 사에 `UserDao` 클래스만 공급하고 상속을 통해 DB 커넥션 기능을 확장해서 사용하게 했던게 다시 불가능해졌다.

이 이유는 **`UserDao`의 코드가 `SimpleConnectionMaker`라는 특정 클래스에 '종속'되어 있기 때문**이다.

결국 DB 커넥션 생성 방법을 변경하려면 `UserDao`의 소스코드를 함께 제공하지 않고는 바꿀 수 없게된 것이다.

상속을 이용했을 때와 마찬가지로 자유로운 확장이 가능하게 하려면 두 가지 문제를 해결해야 한다.

1. `SimpleConnectionMaker`의 메소드가 문제다.  
    DB 커넥션 제공 클래스가 다른 이름을 사용한다면, `UserDao` 내부의 메소드들이 전부 변경되어야 한다.
2. DB 커넥션을 제공하는 클래스가 어떤 것인지를 `UserDao`가 구체적으로 알고 있어야 한다.
    클래스 타입의 인스턴스 변수까지 알고 있으니, 다른 클래스를 구현한다면 전부 변경해야 한다.

이런 문제의 근본적인 원인은 `UserDao`가 바뀔 수 있는 정보, 즉 **DB 커넥션을 가져오는 클래스**에 대해 너무 많이 알고 있기 때문이다.

### 1.3.2 인터페이스의 도입

이 문제의 가장 좋은 해결책은 두 개의 클래스가 서로 긴밀하게 연결되어 있지 않도록 중간에 추상적인 느슨한 연결고리를 만들어주는 것이다.

이를 추상화라고 하고, 추상화는 어떤 것들의 공통적인 성격을 뽑아내어 이를 따로 분리해내는 작업이다.

자바가 추상화를 위해 제공하는 가장 유용한 도구는 바로 **인터페이스**다. 인터페이스는 자신을 구현한 클래스에 대한 구체적인 정보는 모두 감춰버린다. 인터페이스로 추상화해놓은 최소한의 통로를 통해 접근하는 쪽에서는 오브젝트를 만들 때 사용할 클래스가 무엇인지 몰라도 된다.

인터페이스를 사용하면 실제 구현 클래스를 바꿔도 신경 쓸 일이 없다.(제대로 동작하지 않으면 구현 클래스가 잘못된 것이다.)

이제 `UserDao`는 자신이 사용할 클래스가 어떤 것인지 몰라도 된다. 단지 인터페이스를 통해 원하는 기능을 사용하기만 하면 된다.

인터페이스는 어떤 일을 하겠다는 기능만 정의해놓은 것이다. 구체적인 구현방법은 인터페이스를 구현한 클래스들이 알아서 결정하도록 할 일이다.

`UserDao`가 인터페이스를 사용한다면, 인터페이스의 메소드를 통해 알 수 있는 기능에만 관심을 가지면 되지, 그 기능을 어떻게 구현했는지에는 관심을 둘 필요가 없다.

여기에도 아직은 문제가 있다. 바로 `DConnection`이라는 클래스 이름이 보인다. `DConnection` 클래스의 생성자를 호출해서 오브젝트를 생성하는 코드가 여전히 남아있다는 문제다.

어떻게 해야 제거할 수 있을까? 간단한 방법은 아직 보이지 않는다.

### 1.3.3 관계설정 책임의 분리

왜 `UserDao`가 인터페이스뿐 아니라 구체적인 클래스까지 알아야 한다는 문제가 발생하는 것일까?

인터페이스를 이용한 분리에도 불구하고 여전히 `UserDao`변경 없이는 DB 커넥션 기능의 확장이 자유롭지 못한데, 그 이유는 `UserDao`안에 분리되지 않은, 또 다른 관심사항이 존재하고 있기 때문이다.

`new DconnectionMaker()`라는 코드는 매우 짧고 간단하지만 그 자체로 충분히 독립적인 관심사를 담고 있다. 바로 `UserDao`가 어떤 `ConnectionMaker` 구현 클래스의 오브젝트를 이용하게 할지를 결정하는 관심사다.

> 조영호님은 이를 의존성이라는 용어로 칭한다.

간단히 말해, `UserDao`와 `UserDao`가 사용할 `ConnectionMaker`의 구현 클래스 사이의 관계를 설정해주는 것에 대한 관심이다.

이 코드를 분리해야, `UserDao`가 독립적으로 확장 가능한 클래스가 될 수 있다.

두 개의 오브젝트가 있고 한 오브젝트가 다른 오브젝트의 기능을 사용한다면, 사용되는 쪽이 사용하는 쪽에게 서비스를 제공하는 셈이다.

여기서는 `UserDao`가 서비스고, `UserDao`를 사용하는 쪽이 클라이언트다.

갑자기 왜 `UserDao`를 사용하는 쪽의 얘기가 나오냐면, 바로 이곳에 `UserDao`와 `ConnectionMaker` 구현 클래스의 관계를 결정해주는 기능을 분리하기 적절한 곳이기 때문이다.

클래스와 클래스 사이의 관계를 맺는것은 한 클래스가 인터페이스 없이 다른 클래스를 직접 사용한다는 것이고, 오브젝트와 오브젝트 사이의 관계가 우리가 하려는 일이다.

오브젝트 사이의 관계는 런타임시에 한쪽이 다른 오브젝트의 레퍼런스를 갖고 있는 방식으로 만들어진다.

이런 관계는 내부에서 오브젝트를 생성하는 방식으로 관계를 만드는 방법도 있지만, 외부에서 생성된 오브젝트를 가져와서 관계를 맺을 수도 있다.

이런 외부에서 생성된 오브젝트는 생성자나 메소드에서 파라미터로 전달받으면 되는데, 인터페이스를 파라미터 타입으로 지정하면, 어떤 오브젝트던지 그것의 클래스가 해당 인터페이스를 구현했다면 상관없다.

파라미터로 전달받은 오브젝트는 **인터페이스에 정의된 메소드만 사용한다면** 그 오브젝트가 어떤 클래스로부터 만들어졌는지 신경 쓰지 않아도 된다.

단순히 인터페이스로 분리했다고, 끝이 아니라 `UserDao`의 모든 코드들이 `ConnectionMaker` 인터페이스 외에는 어떤 클래스와도 의존관계를 가져선 안된다는 것이 핵심이다.

이것이 가능한 이유는 객체지향 프로그래밍의 **다형성**이라는 특징 덕분이다.

오브젝트 사이의 관계는 런타임 시에 다이내믹하게 런타임 사용관계 또는 링크, 또는 의존관계라고 불리는 관계를 맺어주면 된다.

이는 모델링 시에는 나타나지 않고, 실행 후에 오브젝트로 만들어진 후 나타나게 되는 관계이다.

그렇다면 제 3자인 클라이언트는 무슨 역할을 하는 것일까? 클라이언트는 클래스들을 이용해, 런타임에 오브젝트간의 관계를 만들어주는 책임을 가지고 있다.

클라이언트는 자신이 `UserDao`를 사용하는 입장이기 때문에, 세부 전략인 `ConnectionMaker`의 구현 클래스를 선택하고, 선택한 클래스의 오브젝트를 생성해서 `UserDao`와 연결해줄 수 있다.

기존에는 이 책임이 `UserDao` 생성자에게 있었다. 하지만, 이 책임은 `UserDao`의 관심도 아니고 책임도 아니다. 다른 관심사가 있기 때문에 확장성을 떨어뜨리는 문제가 발생한 것이다.

---

이렇게 해서 `UserDao`는 자신의 관심사이자 책임인 사용자 데이터 액세스 작업을 위해 SQL을 생성하고, 이를 실행하는 데만 집중할 수 있게 됐다.

또한, 이렇게 함으로써 DAO가 아무리 많아져도 DB 접속방법에 대한 관심은 한 군데에 집중되게 되고, 이는 변경시에 한 곳만 수정하면 된다는 의미다.

분리를 아름답게 함으로써 서로 영향을 주지 않으면서 자유롭게 확장할 수 있는 구조가 되었다.

### 1.3.4 원칙과 패턴

잘 알려진 이론을 이용한 작업의 결과를 어떤 장점이 있는지 설명하기 위함.

#### 개방 폐쇄 원칙

개방 폐쇄 원칙은 깔끔한 설계를 위해 적용 가능한 객체지향 설계 원칙 중의 하나로, '클래스나 모듈은 확장에는 열려 있어야 하고, 변경에는 닫혀 있어야 한다'라고 할 수 있다.

초난감 DAO가 대표적인 안티패턴이고, 우리가 개선한 내용은 잘 따르고 있다고 볼 수 있다.

인터페이스르 이용해 확장 기능을 정의한 대부분의 API는 바로 이 개방 폐쇄 원칙을 따른다고 볼 수 있다.

#### 높은 응집도와 낮은 결합도

개방 폐쇄 원칙은 **높은 응집도와 낮은 결합도(high coherence and low coupling)**라는 소프트웨어 개발의 고전적인 원리로도 설명이 가능하다.

응집도가 높다는 건 하나의 모듈, 클래스가 하나의 책임 또는 관심사에만 집중되어 있다는 뜻이다. 하나의 공통 관심사는 한 클래스에 모여 있다.

##### 높은 응집도

변경이 일어날 때 모듈의 많은 부분이 함께 바뀐다면 응집도가 높다고 말할 수 있다.

무엇을 변경해야할 지 명확하고, 다른 클래스의 수정을 요구하지 않고, 기능의 영향을 주지 않는 것이 응집도가 높은 코드다.

##### 낮은 결합도

낮은 결합도는 높은 응집도보다 더 민감한 원칙이다.

책임과 관심사가 다른 오브젝트 또는 모듈과는 낮은 결합도, 즉 느슨하게 연결된 형태를 유지하는 것이 바람직하다.

느슨한 연결은 관계를 유지하는 데 꼭 필요한 최소한의 방법만 간접적인 형태로 제공하고, 나머지는 서로 독립적이고 알 필요도 없게 만들어주는 것이다.

결합도가 낮아지면 변화에 대응하는 속도가 높아지고, 구성이 깔끔해진다. 또한 확장하기에도 매우 편리하다.

여기서 결합도란 '하나의 오브젝트가 변경이 일어날 때에 관계를 맺고 있는 다른 오브젝트에게 변화를 요구하는 정도'라고 설명할 수 있다. 정확히는 오브젝트를 이루는 코드라고 생각된다.

#### 전략 패턴

개선한 코드 구조를 디자인 패턴의 시각으로 보면 **전략 패턴(Strategy Pattern)** 에 해당한다고 볼 수 있다.

전략 패턴은 자신의 기능 맥락에서, 필요에 따라 변경이 필요한 알고리즘을 인터페이스를 통해 **통째로 외부로 분리시키고**, 이를 구현한 구체적인 알고리즘 클래스를 필요에 따라 바꿔서 사용할 수 있게 하는 디자인 패턴이다.

전략 패턴에서 컨텍스트를 사용하는 클라이언트는 컨텍스트가 사용할 전략을 컨텍스트의 생성자 등을 통해 제공해주는 게 일반적이다.

**스프링이란 바로 지금까지 설명한 객체지향적 설계 원칙과 디자인 패턴에 나타난 장점들을 자연스럽게 개발자들이 활용할 수 있게 해주는 프레임워크다.**

## 1.4 제어의 역전(IoC)

IoC 라는 약자로 많이 사용되는 제어의 역전(Inversion of Control)이라는 용어가 있다.

### 1.4.1 오브젝트 팩토리

사실 우리가 앞서 만들었던 것에도 문제가 있다. 바로 `Main`(책에서는 `UserDaoTest`) 클래스가 원래는 테스트 용도의 클래스였는데, 또 다른 책임을 떠맡게 된 것이다.

즉 이것도 분리의 대상이다. 이렇게 해서 분리될 기능은 `UserDao`와 `ConnectionMaker` 구현 클래스의 오브젝트를 만드는 것과, 그렇게 만들어진 두 개의 오브젝트가 연결돼서 사용될 수 있도록 관계를 맺어주는 것이다.

#### 팩토리

분리시킬 기능을 담당할 클래스를 만들어보겠다. 이 클래스의 역할은 객체의 생성 방법을 결정하고 그렇게 만들어진 오브젝트를 돌려주는 것인데, 이런 일을 하는 오브젝트를 흔히 팩토리라고 부른다.

이는 디자인 패턴에서 얘기하는 추상 팩토리 패턴이나 팩토리 메소드 패턴과는 다르니 혼동하지 말자.

단지 오브젝트를 생성하는 쪽(팩토리)과 생성된 오브젝트를 사용하는 쪽(테스트용)의 역할과 책임을 깔끔하게 분리하려는 목적으로 사용하는 것이다.

---

`DaoFactory`는 이제 이미 설정된 `UserDao` 오브젝트를 돌려준다.

#### 설계도로서의 팩토리

`DaoFactory`는 애플리케이션의 오브젝트를 구성하고 그 관계를 정의하는 책임을 맡고 있음을 알 수 있다. 따라서, 애플리케이션을 구성하는 컴포넌트의 구조와 관계를 정의한 설계도 역할을 한다고 볼 수 있다.

간단히 어떤 오브젝트가 어떤 오브젝트를 사용하는지를 정의해놓은 코드라고 생각하면 된다. 이런 작업이 애플리케이션 전체에 걸쳐서 나타난다면, 컴포넌트의 의존관계에 대한 설계도와 같은 역할을 하게 될 것이다.

`DaoFactory`를 분리했을 때 얻을 수 있는 장점은 매우 다양하다. 그중에서도 애플리케이션의 컴포넌트 역할을 하는 오브젝트와 애플리케이션의 구조를 결정하는 오브젝트를 분리했다는 데 가장 의미가 있다.

## 5장

User Table 변경

| 필드명    | 타입        | 설정     |
|-----------|-------------|----------|
| Level     | VARCHAR(10) | Not null |
| Login     | INT         | Not null |
| Recommend | INT         | Not null |

```mysql
ALTER TABLE USER
    ADD level VARCHAR(10) NOT NULL;

ALTER TABLE USER
    ADD login INT NOT NULL;

ALTER TABLE USER
    ADD recommend INT NOT NULL;
```

### 메일 기능 추가

User Table 변경

| 필드명    | 타입         | 설정     |
|-----------|--------------|----------|
| email     | VARCHAR(255) | Not null |

```mysql
ALTER TABLE USER
    ADD email VARCHAR(255) NOT NULL;
```
