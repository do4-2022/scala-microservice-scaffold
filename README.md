# SMS - Scala Microservice Scaffolding

A [Giter8][g8] template for a microservice app in scala!
## How to test locally?

### Prerequisites

- [g8](https://www.foundweekends.org/giter8/setup.html) installed
- [sbt](https://www.scala-sbt.org/download.html) installed

### Run the template

Go to the scala-microservice-scaffold folder and run the following command:

> **Note:** Please change the path to the path of the scala-microservice-scaffold folder on your computer.

```bash
g8 file:///home/toto/projects/scala-microservice-scaffold/ --force
```

> **Note:** Please use 'target' as the name of the project.

This will create a folder named target with the generated project.

### Compile it

To compile it, go to the target folder and run the following command:

```bash
sbt compile
```

This will compile the project and download all the dependencies.

Template license
----------------
Written in 2023 by Polytech DO's students

To the extent possible under law, the author(s) have dedicated all copyright and related
and neighboring rights to this template to the public domain worldwide.
This template is distributed without any warranty. See <https://creativecommons.org/publicdomain/zero/1.0/>.

[g8]: https://www.foundweekends.org/giter8/
