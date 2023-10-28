# Takeout Platform
Reggie Takeout is a software product customized for catering companies, including two parts: system management 
background and mobile application. The system management background is mainly provided for the internal employees 
of the enterprise, and can manage and maintain the internal dishes, packages, and orders.Mobile applications are mainly used by consumers, 
who can browse dishes online, add shopping carts, place orders, etc.

## Main Tools
* **MySQL**: database   
* **Maven**: Project Management   
* **SpringBoot**: Web development framework   
* **MyBatis Plus**: Database Interaction   
* Java backend   
  * **Servlet**: 
    * filter: login verification
    * HttpServletRequest/HttpServletResponse: handling http request and response
* cache:
  * **Redis & spring_cache**: information cache to avoid frequent database access
* Authorization
  * **Session**: store user ID
  * **Cookie**: store session ID
  * **JWT**: (deprecation) backend management identity verification
  * **JavaMailSender**: send random verification code to user email
* Frontend
  * **Vue.js**: user interface
  * **Axios**: HTTP request handling
  * **ElementUI**: user interface
* Other
  * **DigestUtils.md5DigestAsHex**: password encryption
  * **threadLocal**: store user id within same request
  * **MetaObjectHandler & @TableField**:  automatically data processing
  * **ExceptionHandler**:  global exception handler
  * **ObjectMapper**: change the object serialization method
  * URL routing：
    * Method 1: **ResourceHandlerRegistry**
    * Method 2: **Nginx**


## Preview
#### System Management Background
![Video](https://github.com/yuxinwang0279/reggie_take_out/assets/110021928/423340f0-68ad-4c56-a46a-14bb32c8b2d9)

#### Mobile Application
![Video](https://github.com/yuxinwang0279/reggie_take_out/assets/110021928/679c9b47-446d-4532-8932-5ee54ee214b1)


## Further Implementation
