# Shed B

## Features

** Core Of Language
- Struct?
  - immutable
- Function
- composition "operation"
  - language construct?

** Typing
- Staticly typed
- Structural typing
- Labels are unique

** Immutable

## Syntax

    module Messaging {

      label `sent : Date

      shape Message = { sent }

      val message = {
        sent = now()
      } : Message

      fun f (message: Message & Blah & { foo }) {

      }

      val foo = ...
      val x = y => @{
        `sent = now
        `foo  = foo
        `bar  = foo
      }

      val a = { `x = 1 }
      a.x
      val b = { `y = 2 }
      val c = a & b
    }

    // A.xxx
    module A {

      val message = {
        sent = now()
      } // Doesn't compile outside of Messaging
        // Need to explicitly reference Messaging.sent (or import)
        // Or need to fill a hole that is of Message shape like so:
    }

    // B.xxx
    import Messaging.*

    module B {

      val message: Message = {
        sent = now()
      } : Message
    }

# Misc Notes

label foo: int

interface HasFoo {
  foo
}

<foo: int ...>
<foo: string ...>

Iterator.hasNext

interface Iterator[T] {
  boolean hasNext();
  T next();
}

interface Iterable[T] {
  Iterator[T] iterator();
}

def emptyList[T]() {
  return {
    var index = 0;
    implements Iterable[T] {
      iterator: () =>
    }
  }
}

interface Iterator {
  boolean hasNext();
  Object next();
}

struct {
  hasNext: () => ...,
  next: () => ...
}

module Messaging {

  label headers: List[String]

  interface Email {
    headers;
  }
}

interface Email extends Headered {
  List[String] headers = Headered.headers
  String body;
  String address;
}

interface Message {
  List[String] Messaging.headers;
  String body;
}

struct Foo {
  List[String]'Messaging headers;
  String body;
} is Email

var foo = Foo.new(headers = ["",""], foo = "asdf")

var foo = {
  implements Headered {
    headers = value;
  }
  String foo;
}

interface Headered {
  List[String] headers;
}

def writeHeaders(headered: Headered) {
}

def writeHeaders(email: Email) {
}

def writeHeaders(headered: <headers: List[String]>) {
}



label sent: Instant;


def f({x: int, y: String})

def f(f: Foo) {
}



//interface Event {
  String title;
//}

//class TextMessage <: Event {
  String body;
//}

interface sendEvent(Event) {
   sendEvent({event, ...author});
}

interface sendEvent(EventWithAuthor) {
  val eventWithDate = {...event, sent: now()};
  val id = saveEvent(eventWithDate);
  return {...eventWithDate, id};
}



module Messaging {
  label sent: Date;

  shape Message = < sent >

  val message = {
    sent = now()
  }

  f(message: Message & Blah & { foo }) {
  }
}

val message = {
  sent = now()
} : Message

val m = { sent = now() }
m.sent

def f() {
  return {sent = now()}
}


val n: Message = auto m

f(m)

module Email {
  label sent: Date;
}

shape Conversation {
  first: Message
  second: Message
}

def f(message0: Message & A, message1: Message & B) : Conversation[A, B] {
  return Conversation(message0, message1)
}

def f(message: Message) : Message {
  return {sent: message.sent}
}

val list: List[Message & X]
list.map(f): List[Message & X]


message: <sent>
obj = {sent: now()}
message & obj


fun x (): { `a, ...b } {
  if ... @{ `a = 1 } else @{ `a = 2, `b = 3 }
}

x() & { `b = 4 }


# ANTLR Notes

    java -jar ~/apps/antlr-4.5-complete.jar Shed.g4
    javac -cp ~/apps/antlr-4.5-complete.jar Shed*.java
    java -cp .:/home/pair/apps/antlr-4.5-complete.jar org.antlr.v4.runtime.misc.TestRig Shed prog -tree
