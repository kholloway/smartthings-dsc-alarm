smartthings-dsc-alarm
=====================
----

Author: Kent Holloway \<drizit at gmail dot com\>

Smartthings code for DSC (or generic) alarm panels via REST API

Smartthings support is beta status right now follow the rough steps below to get it setup.
Requirements:
  Application to send Alarm codes/events via REST API
  I'm using AlarmServer to do this, look at the smarrthings branch for beta code:
    https://github.com/juggie/AlarmServer

**Note:** Smartthings support is only available in the smartthings branch and not currently in the master branch. Switch to that branch to use it.

### Install AlarmServer on your server/computer

#### Python Method:(Python 2.X required)

I'm currently supporting both DSC panels and Ademco Vista panels via the Envisalink TPI.
Make sure you clone the correct AlarmServer project for your specific panel.

##### For DSC Panels clone the AlarmServer repo:

    git clone https://github.com/juggie/AlarmServer.git
    cd AlarmServer

Switch to the Smartthings Branch in the AlarmServer repo project:

    git checkout smartthings

##### For Ademco/Vista panels clone the HoneyAlarmServer repo:

    git clone https://github.com/MattTW/HoneyAlarmServer

Look in the plugins-example directory for the Smartthings plugin and configure as needed.

##### For all Panel types:

Read the included README.md for the repo you cloned and follow it's setup directions.

Both projects require at least one additional Python module as shown below.
Install the required requests python module:

    pip install requests

Test run AlarmServer.py and make sure you don't get any errors:

    ./alarmserver.py

AlarmServer should work at this point and not complain about anything and you should be able to open a web browser up to your computer/server on HTTPS port 8111 and see the AlarmServer web page.
If that all works then proceed on to the Smarttthings setup.

#### Node.JS Method (NEW!):

https://github.com/oehokie/NodeAlarmProxy

Then see NAP-Demo/demo.js (may need some tweaking).  With NAP-Demo setup it enables enabling/disabling the alarm via a separate device panel, DSC Alarm Thing. (including zero-entry delay "night" mode)


### Setup a Smartthings developer account at:

 [https://graph.api.smartthings.com](https://graph.api.smartthings.com)


### Setup device types

Using the Smartthings IDE create 3 new device types using the code from the devicetypes directory.

There are 4 types of devices you can create:

* DSC Panel       - (Shows partition status info)
* DSC ZoneContact - (contact device open/close)
* DSC ZoneMotion  - (motion device active/inactive)
* DSC Alarm Thing - (w/Node.JS method allows arming/disarming the alarm + night mode, manual refresh of data)

In the Web IDE for Smartthings create a new device type for each of the above devices and paste in the code for each device from the corresponding groovy files in the repo.

You can name them whatever you like but I recommend using the names above 'DSC Panel', 'DSC ZoneContact', 'DSC ZoneMotion', 'DSC Thing' (I'm not creative - oehokie), since those names directly identify what they do.

For all the device types make sure you save them and then publish them for yourself.

### Create panel device

Create a new device and choose the type of "DSC Panel" that you published earlier. The network id needs to be **partition1**.

### Create individual zones
Create a new "Zone Device" for each Zone you want Smartthings to show you status for. 

The network id needs to be the word 'zone' followed by the matching zone number that your DSC system sees it as.

For example: **zone1** or **zone5**


### The rest of the setup

1. Create a new Smartthings App in the IDE, call it 'DSC Integration' or whatever you like. Use the code from dscAlarmIntegrationSmarththingsApp.groovy file for the new smartapp.

2. Click "Enable OAuth in Smart App" and copy down the generated "OAuth Client ID" and the "OAuth Client Secret", you will need them later to generate an access code.
   Click "Create" and when the code section comes up select all the text and replace it with the code from the file 'dscAlarmIntegrationSmarththingsApp.groovy'.
   Click "Save" then "Publish" -> "For Me".

2. Now the hard part, we need to authorize this Smarttthings app to be used via the REST API.
   It's going to take a few steps but all you need is a web browser and your OAuth ID's from the app setup page.
   Follow the RESTAPISetup.md document in this same repo to finish the setup.

3. Edit 'alarmserver.cfg' and add in the OAuth/Access Code information, adjust your zones/partitions and callback event codes as needed.
   Leaving them at the defaults is likely what you already want.

4. Fire up the AlarmServer, you should see your events from the server show up within 1-2 seconds on your Smartphone.

