/*
 *  DSC Panel
 *
 *  Author: Kent Holloway <drizit@gmail.com>
 *  Date: 2014-03-20
 */

// for the UI
metadata {
  // Automatically generated. Make future change here.
  definition (name: "DSC Panel", author: "drizit@gmail.com") {
    // Change or define capabilities here as needed
    capability "Refresh"
    capability "Contact Sensor"
    capability "Polling"

    // Add commands as needed
    command "zone"
    command "partition"
  }

  simulator {
    // Nothing here, you could put some testing stuff here if you like
  }

  tiles {
    // Update all tiles below to match your partition numbers and zone numbers.
    // You have to change both the "zone1" name and the "device.zone1" type to match your zone
    // Then edit the details line further down in this section to match
    // all the zone numbers you setup or the icons will be missing in your app
    // You can add more rows if needed, just copy/paste the standardTile lines
    // for more

    // Final note: if you add/remove/change zones you have to force quit your
    // smartthings app on iOS (probably on Android also) to see the new tile layout after publishing your device

    // First Row
    standardTile("partition1", "device.partition1", width: 2, height: 2, canChangeBackground: true, canChangeIcon: true) {
      state "armed",     label: 'Armed',      backgroundColor: "#79b821", icon:"st.Home.home3"
      state "exitdelay", label: 'Exit Delay', backgroundColor: "#ff9900", icon:"st.Home.home3"
      state "entrydelay",label: 'EntryDelay', backgroundColor: "#ff9900", icon:"st.Home.home3"
      state "notready",  label: 'Open',       backgroundColor: "#ffcc00", icon:"st.Home.home2"
      state "ready",     label: 'Ready',      backgroundColor: "#79b821", icon:"st.Home.home2"
      state "alarm",     label: 'Alarm',      backgroundColor: "#ff0000", icon:"st.Home.home3"
    }
    // 1st row far right icon
    standardTile("zone1", "device.zone1", canChangeBackground: true, canChangeIcon: true) {
      state "open",   label: 'Frnt Door', icon: "st.contact.contact.open",   backgroundColor: "#ffa81e"
      state "closed", label: 'Frnt Door', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
      state "alarm",  label: 'Frnt Door', icon: "st.contact.contact.open",   backgroundColor: "#ff0000"
    }
    // 2nd row far right icon
    standardTile("zone2", "device.zone2", canChangeBackground: true, canChangeIcon: true) {
      state "open",   label: 'GR Entry', icon: "st.contact.contact.open",   backgroundColor: "#ffa81e"
      state "closed", label: 'GR Entry', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
      state "alarm",  label: 'GR Entry', icon: "st.contact.contact.open",   backgroundColor: "#ff0000"
    }
    // third row from left to right
    standardTile("zone3", "device.zone3", canChangeBackground: true, canChangeIcon: true) {
      state "open",   label: 'GR Service', icon: "st.contact.contact.open",   backgroundColor: "#ffa81e"
      state "closed", label: 'GR Service', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
      state "alarm",  label: 'GR Service', icon: "st.contact.contact.open",   backgroundColor: "#ff0000"
    }
    standardTile("zone15", "device.zone15", canChangeBackground: true, canChangeIcon: true) {
      state "open",   label: 'Garage1', icon: "st.doors.garage.garage-open",   backgroundColor: "#ffa81e"
      state "closed", label: 'Garage1', icon: "st.doors.garage.garage-closed", backgroundColor: "#79b821"
      state "alarm",  label: 'Garage1', icon: "st.doors.garage.garage-open",   backgroundColor: "#ff0000"
    }
    standardTile("zone16", "device.zone16", canChangeBackground: true, canChangeIcon: true) {
      state "open",   label: 'Garage2', icon: "st.doors.garage.garage-open",   backgroundColor: "#ffa81e"
      state "closed", label: 'Garage2', icon: "st.doors.garage.garage-closed", backgroundColor: "#79b821"
      state "alarm",  label: 'Garage2', icon: "st.doors.garage.garage-open",   backgroundColor: "#ff0000"
    }

    // Fourth row from left to right
    standardTile("zone17", "device.zone17", canChangeBackground: true, canChangeIcon: true) {
      state "open",   label: 'Sliding', icon: "st.contact.contact.open",   backgroundColor: "#ffa81e"
      state "closed", label: 'Sliding', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
      state "alarm",  label: 'Sliding', icon: "st.contact.contact.open",   backgroundColor: "#ff0000"
    }
    standardTile("zone18", "device.zone18", canChangeBackground: true, canChangeIcon: true) {
      state "open",   label: 'Glass', icon: "st.contact.contact.open",   backgroundColor: "#ffa81e"
      state "closed", label: 'Glass', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
      state "alarm",  label: 'Glass', icon: "st.contact.contact.open",   backgroundColor: "#ff0000"
    }
    // BlankTile that does nothing, just cleans up this row
    standardTile("blanktile", "device.blanktile", width: 1, height: 1, decoration: "flat") {
      state "blank",  label: "", icon: ""
    }

    // Fifth Row
    // Not used any more..
    /*
      standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat") {
        state "default", action:"polling.poll", icon:"st.secondary.refresh"
      }
    */

    // This tile will be the tile that is displayed on the Hub page.
    main "partition1"

    // These tiles will be displayed when clicked on the device, in the order listed here.
    details(["partition1", "zone1", "zone2", "zone3", "zone15", "zone16", "zone17", "zone18", "blanktile"])
  }
}

// parse events into attributes
def parse(String description) {
  // log.debug "Parsing '${description}'"
  def myValues = description.tokenize()

  // log.debug "Description: ${myValues[0]} - ${myValues[1]}"
  sendEvent (name: "${myValues[0]}", value: "${myValues[1]}")
}

// handle commands
def zone(String state, String zone) {
  // state will be a valid state for a zone (open, closed, alarm)
  // zone will be a number for the zone
  log.debug "Zone: ${state} for zone: ${zone}"
  sendEvent (name: "zone${zone}", value: "${state}")
}

def partition(String state, String partition) {
  // state will be a valid state for the panel (ready, notready, armed, etc)
  // partition will be a partition number, for most users this will always be 1
  log.debug "Partition: ${state} for partition: ${partition}"
  sendEvent (name: "partition${partition}", value: "${state}")
}

def poll() {
  log.debug "Executing 'poll'"
  // TODO: handle 'poll' command
  // On poll what should we do? nothing for now..
}

def refresh() {
  log.debug "Executing 'refresh' which is actually poll()"
  poll()
  // TODO: handle 'refresh' command
}


