

MRS is a patient-based medical record system focusing on giving providers a free customizable electronic medical record system (EMR).

The mission of MRS is to improve health care delivery in resource-constrained environments by coordinating a global community that creates a robust, scalable, user-driven, open source medical record system platform.

#### Table of Contents

1. [Build](#build)
   1. [Prerequisites](#prerequisites)
   2. [Build Command](#build-command)
   3. [Deploy](#deploy)
2. [Navigating the repository](#navigating-the-repository)
3. [Software Development Kit](#software-development-kit)
4. [Extending mrs with Modules](#extending-mrs-with-modules)
5. [Documentation](#documentation)
   1. [Developer guides](#developer-guides)
   2. [Wiki](#wiki)
   3. [Website](#website)
6. [Contributing](#contributing)
   1. [Code](#code)
   2. [Code Reviews](#code-reviews)
   3. [Translation](#translation)
7. [Issues](#issues)
8. [Community](#community)
9. [Support](#support)
10. [License](#license)

## Build

### Prerequisites

#### Java

MRS is a Java application which is why you need to install a Java JDK.

If you want to build the master branch you will need a Java JDK of minimum version 8.

#### Maven

Install the build tool [Maven](https://maven.apache.org/).

You need to ensure that Maven uses the Java JDK needed for the branch you want to build.

To do so execute

```bash
mvn -version
```

which will tell you what version Maven is using. Refer to the [Maven docs](https://maven.apache.org/configure.html) if you need to configure Maven.

#### Git

Install the version control tool [git](https://git-scm.com/) and clone this repository with

```bash
git clone https://github.com/mrs/mrs-core.git
```

### Build Command

After you have taken care of the [Prerequisites](#prerequisites)

Execute the following

```bash
cd mrs-core
mvn clean package
```

This will generate the mrs application in `webapp/target/mrs.war` which you will have to deploy into an application server like for example [tomcat](https://tomcat.apache.org/) or [jetty](http://www.eclipse.org/jetty/).

### Deploy

For development purposes you can simply deploy the `mrs.war` into the application server jetty via

```bash
cd mrs-core/webapp
mvn jetty:run
```

If all goes well (check the console output) you can access the mrs application at `localhost:8080/mrs`.

Refer to [Getting Started as a Developer - Maven](https://wiki.mrs.org/display/docs/Maven) for some more information
on useful Maven commands and build options.

## Navigating the repository

The project tree is set up as follows:

<table>
 <tr>
  <td>api/</td>
  <td>Java and resource files for building the java api jar file.</td>
 </tr>
 <tr>
  <td>tools/</td>
  <td>Meta code used during compiling and testing. Does not go into any released binary (like doclets).</td>
 </tr>
 <tr>
  <td>web/</td>
  <td>Java and resource files that are used in the webapp/war file.</td>
 </tr>
 <tr>
  <td>webapp/</td>
  <td>files used in building the war file (contains JSP files on older versions).</td>
 </tr>
 <tr>
  <td>pom.xml</td>
  <td>The main maven file used to build and package mrs.</td>
 </tr>  
</table>

## Software Development Kit

For rapid development of modules and the mrs Platform code check out the
awesome SDK at

https://wiki.mrs.org/display/docs/mrs+SDK

## Extending mrs with Modules

mrs has a modular architecture that allows developers to extend the mrs core functionality by creating modules that can easily be added or removed to meet the needs of a specific implementation.

Before creating your own module go to the [mrs Module Repository](https://modules.mrs.org) and see if there is already a module for your specific use case. If so deploy and try it and if a functionality is missing join the developers of the module to add a feature.

If you haven't found what you were looking for refer to the [Module - wiki](https://wiki.mrs.org/display/docs/Modules) to learn how you can create a new module.

## Documentation

### Developer guides

If you want to contribute please refer to these resources

* [Getting Started as a Developer](https://wiki.mrs.org/display/docs/Getting+Started+as+a+Developer)
* [How To Configure Your IDE](https://wiki.mrs.org/display/docs/How-To+Setup+And+Use+Your+IDE)
* [How To Make a Pull Request](https://wiki.mrs.org/display/docs/Pull+Request+Tips)

### Wiki

If you are looking for detailed guides on how to install, configure, contribute and
extend mrs visit

http://wiki.mrs.org

### Website

If you are looking for more information regarding mrs as an organization
check

http://mrs.org

## Contributing

Contributions are very welcome, we can definitely use your help!

mrs organizes the privileges of its contributors in developer stages which
are documented [here](https://wiki.mrs.org/display/RES/mrs+Developer+Stages).

Read the following sections to find out where you could help.

### Code

Check out our [contributing guidelines](CONTRIBUTING.md), read through the [Developer guides](#developer-guides).

After you've read up :eyeglasses: [grab an introductory issue](https://wiki.mrs.org/display/docs/Getting+Started+as+a+Developer) that is `Ready For Work`.

### Code Reviews

You might not have the time to develop yourself but enough experience with
mrs and/or reviewing code, your help on code reviews will be much
appreciated!

Read

https://wiki.mrs.org/display/docs/Code+Review

and get started with re-:eyes: pull requests!

### Translation

We use

https://www.transifex.com/mrs/mrs/

to manage our translations.

The `messages.properties` file in this repository is our single source of
truth. It contains key, value pairs for the English language which is the
default.

Transifex fetches updates to this file every night which can then be translated
by you and me on transifex website itself. At any time we can pull new translations from transifex
back into this repository. Other languages like for ex. Spanish will then be in
the `messages_es.properties` file.

If you would like to know how to help with translations see

http://mrs.org/join-the-community/translate/

## Issues

If you want help fix existing issues or you found a bug and want to tell us please go to
