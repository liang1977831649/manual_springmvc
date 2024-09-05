一、主要的java技术点：反射、IO文件读写、注解、集合、DOM4J、Tomcat、字符串API
二、底层大致执行流程
1、tomcat启动程序，读出XML文件，加载中央控制器Servlet，web.xml文件中存在一个参数
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:springmvc.xml</param-value>
        </init-param>
2、使用dom4j读出<param-value>classpath:springmvc.xml</param-value>，getServletContext().getRealPath("/")获取静态资源路径，找到springmvc.xml 文件
3、读出springmvc.xml文件中写明的要装配的类的类路径
4、使用IO流读出类路径下面的所有装配的类的绝对路径，判断这些类绝对路径下面是否有Service/Controller等注解，如果存在，那就注入到ioc容器（本质就是hashMap）
5、处理AutoWire注解修饰的属性，从ioc容器中得到Controller类对象、Service类对象，看他们的属性是否被AotuWire修饰，如是，就从ioc中找到这个属性对应的对象，使用反射爆破将其赋值上去
6、处理HandlerRequestMapping，扫描ioc容器中的Controller类对象，将被RequestMapping修饰的方法，映射路径，controller类对象封装到handler中，在封装到List<Handler>集合中
7、
