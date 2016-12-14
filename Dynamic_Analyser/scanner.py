
#this scripts inly checks for single a single file
#reads files from /var/www/html/App/uploads/
import os
import time

path = "/var/www/html/App/uploads/"

for file in os.listdir(path):
    if file.endswith(".apk"):
        print(file)

full_path = path+file
print "Apk found at path :"

print full_path

#run against droidbox


#os.system('export PATH=$PATH:/home/j0nee/Android/Sdk/tools')
#os.system('export PATH=$PATH:/home/j0nee/Android/Sdk/platform-tools')
os.system('./startemu.sh droid_test')
#waiting for avd to  boot up
time.sleep(100)
print "Starting dynamic analysis"

com = './droidbox.sh '+full_path
#Dynamic analysis
os.system(com)
