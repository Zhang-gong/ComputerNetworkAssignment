#!/usr/bin/python3

import os

def get_cgi_env():
    os.remove("cgi.html")
    f=open("cgi.html","w")
    i = 0
    f.write("Content-type:text/html\r\n\r\n")
    f.write("<html>\n")
	
    f.write("<head>\n")
    f.write("<title> CGI Envrionment  Variables</title>\n")
    f.write("</head>\n")
    
    f.write("<body>\n")
    f.write("<table border =\"0\" cellspacing=\"2\">")

    for env in os.environ.keys():
        f.write("<tr>")
        f.write("<td> %s </td>" %(env))
        f.write("<td>")
    # value = os.environ[env]
    # if NULL != value:
        f.write(os.environ[env])
    # else:
    #     print("Environment variable does not exist.")
	
        f.write("</td>")
        f.write("</tr>\n")
	
    f.write("</table>")
    f.write("</body>\n")
    f.write("</html>\n")
    f.close()

if __name__ == '__main__':
     get_cgi_env()