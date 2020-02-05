<!DOCTYPE html>
<html>
<head>
    <meta charset="utf‐8">
    <title>Hello World!</title>
</head>
<body>
Hello ${name}!
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
    <#list stus as stu>
        <tr>
            <td>${stu_index + 1}</td>
            <td>${stu.name}</td>
            <td>${stu.age}</td>
            <td <#if stu.money gt 500>style="background-color: aqua;" </#if> >${stu.money}</td>
        </tr>
    </#list>
</table>
<br/>

输出stu1的学生信息：<br/>
姓名: ${stu1.name} <br/>
年龄: ${stu1.age}  <br/><br/>

输出stu2的学生信息：<br/>
姓名: ${stuMap["stu2"].name} <br/>
年龄: ${stuMap["stu2"].age}  <br/><br/>

空值处理: <br/>
空值: ${kong!"我是空值"} <br/><br/>

日期格式化: <br/>
生日: ${stu1.birthday?date} <br/>
生日: ${stu1.birthday?string("YYYY年MM月DD日")} <br/><br/>

内建函数c: <br/>
数字: ${1234567890} <br/>
字符串: ${1234567890?c} <br/><br/>

将json字符串转成对象: <br/>
<#assign coder="{'name': 'jiavg', 'age': '23', 'profession': 'student'}"/>
<#assign coderObj=coder?eval/>
昵称: ${coderObj.name} <br/>
年龄: ${coderObj.age} <br/>
职业: ${coderObj.profession} <br/>

</body>
</html>