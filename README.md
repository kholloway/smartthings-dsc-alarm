smartthings-dsc-alarm
=====================

Author: Kent Holloway <drizit at gmail dot com>

Smartthings code for DSC (or generic) alarm panels via REST API

Smartthings support is beta status right now follow the rough steps below to get it setup.
Requirements:
  Application to send Alarm codes/events via REST API
  I'm using AlarmServer to do this, look at the smarrthings branch for beta code:
    https://github.com/juggie/AlarmServer

**Note:** Smartthings support is only available in the smartthings branch and not currently in the master branch. Switch to that branch to use it.

1. Setup a Smartthings developer account at [Smartthings Developers](https://graph.api.smartthings.com)

2. Create a new Device Type in the IDE, call it 'DSC Panel' or whatever you like, you only need name for the device ignore all the other options and click "Create" at the bottom. Once the Code section pops up highlight all the code that was created for you and paste in the code from the file 'dscPanelDeviceSmartthings.groovy' in this repo. Click "Save" then "Publish" -> "For Me". This 'DSC Panel' device is highly configurable but only directly in the code right now. Change the zone numbers on the standardTiles to match the zones coming from AlarmServer. Two of my zones are GarageDoors, remove them or change them to the standard open/close device as needed (copy/paste the code from another standard zone).

3. Create a new Smartthings App in the IDE, call it 'DSC Integration' or whatever you like. Click "Enable OAuth in Smart App" and copy down the generated "OAuth Client ID" and the "OAuth Client Secret", you will need them later to generate an access code. Click "Create" and when the code section comes up select all the text and replace it with the code from the file 'dscAlarmIntegrationSmarththingsApp.groovy'.  Click "Save" then "Publish" -> "For Me".

4. On your Smartphone or in the WebUI create a new Device call it "Alarm Panel", assign it to your location and your hub then click the dropdown menu for the devicetypes at the bottom and pick the "DSC Panel" device you created earlier (it will be at the very bottom of the deviceTypes list).

5. Now the hard part, we need to authorize this Smarttthings app to be used via the REST API.
   It's going to take a few steps but all you need is a web browser and your OAuth ID's from the app setup page.
   Follow the RESTAPISetup.md document in this same repo to finish the setup.

6. Edit 'alarmserver.cfg' and add in the OAuth/Access Code information, adjust your zones/partitions and callback event codes as needed. Leaving them at the defaults is likely what you already want.

7. Create a "Zone Device" for each Zone you want to display individually.  The network id needs to be "zone#" (example: zone1).  You will be able to add this to the dashboard.  Once you make the device names, go into the SmartApp settings and add the Zone Devices to the Zone Device List.  You'll need to refresh the data somehow for it to work on the dashboard (restart your alarmserver script, for example)

8. Fire up the AlarmServer, you should see your events from the server show up within 1-2 seconds on your Smartphone.

