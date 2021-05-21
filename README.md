[![](https://jitpack.io/v/liccioni/transformTo.svg)](https://jitpack.io/#liccioni/transformTo)

# TransformTo

TransformTo is a Java library for dealing with generic transformations and generic handling.

## Installation

Gradle.

```groovy
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
    dependencies {
        implementation 'net.liccioni:transformTo:v0.0.1'
    }
```

## Usage

```java

class Application {
    public static void main(String[] args) {
        
        Transformable.register(String.class, Float.class, Float::parseFloat);
        Transformable aNumber = Transformable.toTransformable("42");
        Float actual = aNumber.transformTo(Float.class);
        Float expected = 42f;
        assertThat(actual).isEqualTo(expected);

        AtomicReference<String> actual = new AtomicReference<>();
        Observable.register(String.class).then(actual::set);
        Observable.send("hello!");
        assertThat(actual.get()).isEqualTo("hello!");
    }
}

```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[APACHE-2.0](https://www.apache.org/licenses/LICENSE-2.0.html)