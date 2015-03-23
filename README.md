# Spring Security Sample Application with JavaConfig

This is a sample application using with Spring Security, Spring MVC and MyBatis3.
This application is configured using the JavaConfig mechanism of JavaEE and Spring Framework.

* Spring Security : [3.2.5.RELEASE](http://docs.spring.io/spring-security/site/docs/3.2.5.RELEASE/reference/htmlsingle/) (Mainly)
* Spring MVC : [4.1.4.RELEASE](http://docs.spring.io/spring/docs/4.1.4.RELEASE/spring-framework-reference/htmlsingle/)
* MyBatis : [3.2.8](http://mybatis.github.io/mybatis-3/)
* And more ...

> **Note:**
>
> The version of library except the MyBatis has been managed using [Spring IO Platform 1.1.1.RELEASE](http://docs.spring.io/platform/docs/1.1.1.RELEASE/reference/htmlsingle/).


# How to build and run with the Tomcat

## Gradle

```bash
$ gradle clean TomcatRun
```

## Maven

```bash
$ mvn clean tomcat7:run
```

# How to access to the top page

Please access to the [http://localhost:8080/spring-security-app/](http://localhost:8080/spring-security-app/).

# Describe a configuration of web application

| Configuration Module | Description
| ---- | ---- |
| [`springsecurity.WebApplicationInitializer`](https://github.com/kazuki43zoo/spring-security-app/blob/master/src/main/java/springsecurity/WebApplicationInitializer.java) | This class is configure the global setting of web application (such as the Servlet Filter). |
| [`springsecurity.app.AppServletInitializer`](https://github.com/kazuki43zoo/spring-security-app/blob/master/src/main/java/springsecurity/app/AppServletInitializer.java) | This class is configure the servlet of screen flow processing. |
| [`springsecurity.api.ApiServletInitializer`](https://github.com/kazuki43zoo/spring-security-app/blob/master/src/main/java/springsecurity/api/ApiServletInitializer.java) | This class is configure the servlet of REST APIs processing. |
| [`web.xml`](https://github.com/kazuki43zoo/spring-security-app/blob/master/src/main/webapp/WEB-INF/web.xml) | This xml file is configure only the JSP setting. |
