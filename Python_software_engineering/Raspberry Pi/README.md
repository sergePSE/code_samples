<h1>Raspberry PI project part</h1>

<h2>Requirements</h2>
Project has to :

<ul>
    <li>Connect to a webcam.</li>
    <li>Recognize the QR code.</li>
    <li>Parse QR code to string</li>
    <li>Parse string to attandance record</li>
    <li>Get information about user from the server</li>
    <li>Try post attendance to server</li>
    <li>Display the result on PI screen</li>
</ul>

<h2>Description</h2>

As an input system checks the camera. Every frame is checked for a new QR code.
With 'debug' argument it's possible to see camera view.

QR code is stored in temporary buffer within settings.code_repetition_time_sec(3). Then, attendance record is filled 
with additional information from server. Filled attendance can be posted on server. Depending on http code writes
attendance information ('student name, course name \n datetime, status of registration (OK, already posted, 
not registered or failed)')

QR code contains fields as in the next example "4785074604081152,185804764220139124118", where 
"<attendance_id/>,<student_id/>"

Tutor must mention his group and tutor id in a settings.py file. It has a numerous of hardcoded settings. If weekId and
groupId are not mentioned, that it can be requested with get requests from the server. WeekId should be a number from
0 to 12. 

Then full attendance record is posted on the server in a format of the next XML file. Token is stored in attendance id.

```
<attendance>
   <attendance_id>4785074604081152</attendance_id>
   <student_id>185804764220139124118</student_id>
   <group_id>6192449487634432</group_id>
   <week_id>0</week_id>
   <presented>true</presented>
</attendance>
```
As the result Raspberry PI returns message result.

Project was build with use of code base from:  
https://github.com/cuicaihao/Webcam_QR_Detector/blob/master/Lab_01_QR_Bar_Code_Detector_Basic.ipynb


<h2>Dependencies</h2>
<ul>
    <li>libzbar-dev - qr code decoder</li>
</ul>
sudo apt-get install libzbar-dev (or download http://zbar.sourceforge.net/ and install)<br>
<ul>
    <li>PiFace</li>
</ul>
<details>
    <summary>Manually without raspberrian repository</summary>
    
    git clone https://github.com/tompreston/python-lirc.git
    cd python-lirc/
    make py3 && sudo python3 setup.py install
    
    git clone https://github.com/piface/pifacecommon.git
    cd pifacecommon/
    sudo python3 setup.py install
    
    git clone https://github.com/piface/pifacecad.git
    cd pifacecad/
    sudo python3 setup.py install
</details>
<details>
    <summary>Raspberrian repository</summary>
    install via apt
</details>

<ul>
    <li>opencv - recognition of qr code on photo</li>
    <li>sudo apt-get install libopencv-dev</li>
</ul>
<details>
    <summary>For example</summary>
    sudo date -s "$(wget -qSO- --max-redirect=0 google.com 2>&1 | grep Date: | cut -d' ' -f5-8)Z"
    sudo apt-get -y install python-opencv
    pip3 install opencv-python
    sudo apt-get -y install libcblas-dev
    sudo apt-get -y install libhdf5-dev
    sudo apt-get -y install libhdf5-serial-dev
    sudo apt-get -y install libatlas-base-dev
    sudo apt-get -y install libjasper-dev 
    sudo apt-get -y install libqtgui4 
    sudo apt-get -y install libqt4-test
    sudo apt-get -y install libjasper-dev
    sudo apt-get install -y cmake git libgtk2.0-dev pkg-config libavcodec-dev libavformat-dev libswscale-dev
    sudo apt-get install -y python-dev python-numpy libtbb2 libtbb-dev libjpeg-dev libpng-dev libtiff-dev libjasper-dev libdc1394-22-dev
    pip3 install -r requirements.txt
    
</details>

<h2>Usage:</h2>
run python3 ./main.py [debug] #debug is optional! 
 Finish the program with 'q' button 
 
<h2>Dependencies</h2>
<b>D1.Reading QR code with the camera.</b> 


QR code should be a text with at least next fields:
"4785074604081152,185804764220139124118", where "<attendance_id/>,<student_id/>"

<b>D2.Register the attendance with the post().</b>

In the body:
```
<attendance>
   <attendance_id>4785074604081152</attendance_id>
   <student_id>185804764220139124118</student_id>
   <group_id>6192449487634432</group_id>
   <week_id>0</week_id>
   <presented>true</presented>
</attendance>
```
As the response:
<ul>
    <li>code 200: Success. Returns the text of error or access.</li>
    <li>or error with the name of the error code, if it's possible</li>
</ul>
attandance_client.py

<b>D3. Getting the course information.</b>
Requesting course and week ids from the main server given a tutor current id.
get(tutorId) -> {courseId, weekId}

<h2>Settings</h2>

Located in settings.py. Host service paths located in raspberry_pi/web_attendance_client/..*client

<h2>Useful commands to install on Raspberry PI</h2>
1) Problem with system clocks, reset to the google server
```sudo date -s "$(wget -qSO- --max-redirect=0 google.com 2>&1 | grep Date: | cut -d' ' -f5-8)Z"```