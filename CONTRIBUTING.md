# Contributing to pxc-framework

pxc-framework-boot3 is released under the Apache 2.0 license. If you would like to contribute something, or want to hack on the code this document should help you get started.

## Code of Conduct

This project adheres to the Contributor Covenant [code of conduct](CODE_OF_CONDUCT.md).
By participating, you are expected to uphold this code. Please report unacceptable behavior to 545685602@qq.com.

## Using GitHub Issues

We use GitHub issues to track bugs and enhancements.

If you are reporting a bug, please help to speed up problem diagnosis by providing as much information as possible.
Ideally, that would include a small sample project that reproduces the problem.

## Reporting Security Vulnerabilities

we've had a time to fix it.

## Sign the Contributor License Agreement

Before we accept a non-trivial patch or pull request we will need you to read the Contributor License Agreement.

## Code Conventions and Housekeeping

None of these is essential for a pull request, but they will all help.

They can also be added after the original pull request but before a merge.

* We use the [Spring JavaFormat](https://github.com/spring-io/spring-javaformat/)]project to apply code formatting conventions.
  If you use Eclipse and you follow the '`Importing into eclipse`' instructions below you should get project specific formatting automatically.
  You can also install the [Spring JavaFormat IntelliJ Plugin](https://github.com/spring-io/spring-javaformat/#intellij-idea) or format the code from the Gradle build by running `./gradlew format`.
  Note that if you have format violations in `buildSrc`, you can fix them by running `./gradlew -p buildSrc format` from the project root directory.
* The build includes Checkstyle rules for many of our code conventions. Run `./gradlew checkstyleMain checkstyleTest` if you want to check your changes are compliant.
* Make sure all new `.java` files have a Javadoc class comment with at least an `@author` tag identifying you, and preferably at least a paragraph on what the class is for.
* Add the ASF license header comment to all new `.java` files (copy from existing files in the project).
* Add yourself as an `@author` to the `.java` files that you modify substantially (more than cosmetic changes).
* Add some Javadocs.
* A few unit tests would help a lot as well -- someone has to do it.
* Verification tasks, including tests and Checkstyle, can be executed by running `./gradlew check` from the project root.
  Note that `SPRING_PROFILES_ACTIVE` environment variable might affect the result of tests, so in that case, you can prevent it by running `unset SPRING_PROFILES_ACTIVE` before running the task.
* If no-one else is using your branch, please rebase it against the current main branch (or other target branch in the project).
* When writing a commit message please follow [these conventions](https://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html).
