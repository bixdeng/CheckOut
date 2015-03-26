import serial
import urllib
import urllib2

ser = serial.Serial('/dev/tty.usbmodem1411', 9600)
url = 'http://192.168.43.196/android_connect/sendweight.php'

# while True:
value = ser.readlcdine()
print value
query_args = { 'weight': value }
encoded_args = urllib.urlencode(query_args)
req = urllib2.Request(url, encoded_args)
req.add_header("Content-type", "application/x-www-form-urlencoded")
page = urllib2.urlopen(req).read()
# print page
sys.exit(0);


