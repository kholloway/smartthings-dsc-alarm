/*
 *  DSC Alarm Panel integration via REST API callbacks
 *
 *  Author: Kent Holloway <drizit@gmail.com>
 */

preferences {
  def events = ['alarm','closed','open','closed','partitionready','partitionnotready','partitionarmed','partitionalarm','partitionexitdelay','partitionentrydelay']
  section("Alarm Panel:") {
    input "panel", "device.dSCPanel", title: "Choose Alarm Panel?", multiple: false, required: true
  }
  section("Activate Switches?) {
    input "lights", "capability.switch", title: "Which lights/switches", multiple: true, required: false
  }
  section("Activate alarm?) {
    input "alarms", "capability.alarm", title: "Which Alarm(s)", multiple: true, required: false
  }
  section("Notifications (optional)") {
    input "sendPush", "enum", title: "Push Notifiation", required: false, metadata: [values: ["Yes","No"]]
    input "phone1", "phone", title: "Phone Number", required: false
  }
  section("Notification events:") {
    input "notifyEvents", "enum", title: "Which Events?", description: "default (all)", required: false, multiple: true, options: events
  }
}

mappings {
  path("/panel/:eventcode/:zoneorpart") {
    action: [
      GET: "updateZoneOrPartition"
    ]
  }
}

def installed() {
  log.debug "Installed!"
  subscribe(panel)
}

def updated() {
  log.debug "Updated!"
  unsubscribe()
  subscribe(panel)
}

void updateZoneOrPartition() {
  update(panel)
}

private update(panel) {
    // log.debug "update, request: params: ${params} panel: ${panel.name}"

    // Add more events here as needed
    // Each event maps to a command in your "Alarm Panel" device type
    def eventMap = [
      '601':'alarm',
      '602':'closed',
      '609':'open',
      '610':'closed',
      '650':'partitionready',
      '651':'partitionnotready',
      '652':'partitionarmed',
      '654':'partitionalarm',
      '656':'partitionexitdelay',
      '657':'partitionentrydelay'
    ]
    def zoneorpartition = params.zoneorpart
    def eventCode = params.eventcode
    if (eventCode)
    {
      // Lookup our eventCode in our eventMap
      def command = eventMap."${eventCode}"
      if (command)
      {
        // We have a valid command, lets send it to the device
        panel."$command"(zoneorpartition)
      }
    }
}
