#! /usr/bin/env python3
import os
# print("Content-type:text/html\r\n\r\n")
# print("<html>\n")
# print("<head>\n")
# print("<title>myCGI env</title>\n")
# print("</head>\n")
# print("<table border =\"0\" cellspacing=\"2\">")
for env in os.environ.keys():
    # print("<tr>")
    print(env + "  " + os.environ[env])
    # print("<td>")
# value = os.environ[env]
# if NULL != value:
    # print()
# else:
#     print("Environment variable does not exist.")

#     print("</td>")
#     print("</tr>\n")
# print("</table>")
# print("hello it is a test!")
# print("<body>\n")
# print("</body>\n")
# print("</html>\n")
