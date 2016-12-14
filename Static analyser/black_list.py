
"""
Done as part of mini project demonstration
Check the initial checking of packages
"""

import pycurl
import requests
import time
import json
import yaml
from StringIO import StringIO


print "listening firebase"


#listen for uploding a package //this method is not encouraged by Me....;)\

new_pack = 0;

"""
while(new_pack == 0):
    pack = StringIO()
    padded = pycurl.Curl()
    padded.setopt(padded.URL, 'https://miniproject-cb7e0.firebaseio.com/padded.json?print=pretty')
    padded.setopt(padded.WRITEDATA, pack)
    padded.perform()
    padded.close()
    new_pack = pack.getvalue()
    # Body is a string in some encoding.
    # In Python 2, we can print it without knowing what the encoding is.
    print(new_pack)

"""

mal = 0
#collecting PackageName
name = StringIO()
pname = pycurl.Curl()
pname.setopt(pname.URL, 'https://miniproject-cb7e0.firebaseio.com/PackageName.json?print=pretty')
pname.setopt(pname.WRITEDATA, name)
pname.perform()
pname.close()
package = name.getvalue()

#json to string
package =  yaml.safe_load(package)
print(package)

#collecting MD5 hash
hashval = StringIO()
md = pycurl.Curl()
md.setopt(md.URL, 'https://miniproject-cb7e0.firebaseio.com/MD5Hash.json?print=pretty')
md.setopt(md.WRITEDATA, hashval)
md.perform()
md.close()
has = hashval.getvalue()
# Body is a string in some encoding.
# In Python 2, we can print it without knowing what the encoding is.

#json to string
has = yaml.safe_load(has)
print(has)


"""
name = StringIO()
pname = pycurl.Curl()
pname.setopt(pname.URL, 'https://miniproject-cb7e0.firebaseio.com/PackageName.json?print=pretty')
pname.setopt(pname.WRITEDATA, name)
pname.perform()
#pname.close()
"""

#open file with malicious package names
fo = open("black_list_name","r+")
line = fo.readline()
while line:
    if package in line:
        #data = json.dumps({"Malicious": "1"})
        print "Malicious Package Found"
    line = fo.readline()

fo.close()

f = open("black_list_md5","r+")
line = f.readline()
while line:
    if has in line:
        mal = 1
        #data = json.dumps({"Malicious": "1"})
        pname.perform()
        print "Malicious Package Found"
    line = f.readline()

f.close()

new_pack = 0;
#pname.perform()
#pname.close()
