/*
*  DSC Alarm Panel integration via REST API callbacks
*
*  Author: Kent Holloway <drizit@gmail.com>
*  Modified by: Matt Martz <matt.martz@gmail.com>
*/


// Automatically generated. Make future change here.
definition(
    name: "DSC Alarm Panel App",
    namespace: "",
    author: "Matt Martz",
    description: "DSC Alarm Panel App",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png",
    oauth: true
)

import groovy.json.JsonBuilder

preferences {

    section("Alarm Panel:") {
        input "panel", "capability.polling", title: "Alarm Panel", multiple: false, required: false
    }
    section("Alarm Thing:") {
        input "dscthing", "capability.polling", title: "Alarm Thing", multiple: false, required: false
    }
    section("Zone Devices:") {
        input "zonedevices", "capability.contactSensor", title: "DSC Zone Devices", multiple: true, required: false
    }
    section("Thermostats:") {
        input "thermostats", "capability.thermostat", title: "Thermostats (used below)", multiple: true, required: false    
    }
    section("Things to do when disarmed:") {
        input "disarmMode", "mode", title: "Mode Change?", required: false
        input "thermostatdisarm", "enum", title: "Set Thermostat to Home", required: false,
            metadata: [
                values: ["Yes","No"]
            ]
    }
    section("Things to do when armed away:") {
        input "lightsoff", "capability.switch", title: "Turn lights off?", multiple: true, required: false
        input "sonos", "capability.musicPlayer", title: "Turn sonos off?", multiple: true, required: false
        input "awayMode", "mode", title: "Mode Change?", required: false
        input "thermostataway", "enum", title: "Set Thermostat to Away", required: false,
            metadata: [
                values: ["Yes","No"]
            ]
    }
    section("Things to do when armed night mode:") {
        input "nightMode", "mode", title: "Mode Change?", required: false
    }
    section("Turn things on when ALARMING:") {
        input "lightson", "capability.switch", title: "Which lights/switches?", multiple: true, required: false
        input "alarms", "capability.alarm", title: "Which Alarm(s)?", multiple: true, required: false
    }
    section("Notifications (optional):") {
        input "sendPush", "enum", title: "Push Notifiation", required: false,
            metadata: [
                values: ["Yes","No"]
            ]
        input "phone1", "phone", title: "Phone Number", required: false
        input "notifyalarm", "enum", title: "Notify When Alarming?", required: false,
            metadata: [
                values: ["Yes","No"]
            ]
        input "notifyarmed", "enum", title: "Notify When Armed?", required: false,
            metadata: [
                values: ["Yes","No"]
            ]
        input "locks", "capability.lock", title: "Check Locks and Notify when Armed?", required: false, multiple: true
    }
    section("XBMC Notifications") {
        input "xbmcserver", "text", title: "XBMC IP", description: "IP Address", required: false
        input "xbmcport", "number", title: "XBMC Port", description: "Port", required: false
    }
}

mappings {
    path("/panel/:eventcode/:zoneorpart/:partitionmode") {
        action: [
            GET: "updateZoneOrPartition"
        ]
    }
}

def installed() {
    log.debug "Installed!"
    subscribe(panel)
    subscribe(dscthing, "updateDSC", updateDSC)
}

def updated() {
    log.debug "Updated!"
    unsubscribe()
    subscribe(panel)
    subscribe(dscthing, "updateDSC", updateDSC)
}

void updateZoneOrPartition() {
    update(panel)
}

def updateDSC(evt) {
    log.debug "$evt.value"
    //add code here to parse $evt.value... probably into zonenumber and state
    def evtList = "$evt.value".tokenize();
    if ("${evtList[1]}" == '601') {
        updateZoneDevices(zonedevices,"${evtList[0]}","alarm")
    }
    if ("${evtList[1]}" == '602') {
        updateZoneDevices(zonedevices,"${evtList[0]}","closed")
    }
    if ("${evtList[1]}" == '609') {
        updateZoneDevices(zonedevices,"${evtList[0]}","open")
    }
    if ("${evtList[1]}" == '610') {
        updateZoneDevices(zonedevices,"${evtList[0]}","closed")
    }
}

private update(panel) {
    // log.debug "update, request: params: ${params} panel: ${panel.name}"
    def zoneorpartition = params.zoneorpart

    // Add more events here as needed
    // Each event maps to a command in your "DSC Panel" device type
    def eventMap = [
        '601':"zone alarm",
        '602':"zone closed",
        '609':"zone open",
        '610':"zone closed",
        '650':"partition ready",
        '651':"partition notready",
        '652':"partition armed",
        '654':"partition alarm",
        '656':"partition exitdelay",
        '657':"partition entrydelay",
        '658':"partition lockout",
        '659':"partition failed",
        '655':"partition disarmed"
    ]

    // get our passed in eventcode
    def eventCode = params.eventcode
    log.debug "Event code: ${eventCode}"
    if (eventCode)
    {
        // Lookup our eventCode in our eventMap
        def opts = eventMap."${eventCode}"?.tokenize()
        //      log.debug "Options after lookup: ${opts}"
        //      log.debug "Zone or partition: $zoneorpartition"
        if (opts[0])
        {
            // We have some stuff to send to the device now
            // this looks something like panel.zone("open", "1")
            log.debug "Test: ${opts[0]} and: ${opts[1]} for $zoneorpartition"
            panel."${opts[0]}"("${opts[1]}", "$zoneorpartition")
            if ("${opts[0]}" == 'zone') {
                //log.debug "It was a zone...  ${opts[1]}"
                updateZoneDevices(zonedevices,"$zoneorpartition","${opts[1]}")
            }
            if ("${opts[0]}" == 'partition') {
                log.debug "It was a partition...  ${opts[1]}... ${params.partitionmode}"
                if ("${opts[1]}" == 'disarmed') {
                    if (disarmMode) {
                        setLocationMode(disarmMode)
                    }
                    if (thermostatdisarm == "Yes") {
                        if (thermostats) {
                            for (thermostat in thermostats) {
                                thermostat.present()
                            }
                        }
                    }
                }
                if ("${opts[1]}" == 'alarm') {
                    if (lightson) {
                        lightson?.on()
                    }
                    if (notifyalarm == "Yes") {
                        log.debug "Notify when alarm is Yes and the Alarm is going off"
                        sendMessage("ALARMING")
                    }
                }
                if ("${opts[1]}" == 'armed') {
                    if ("${dscthing.latestValue('alarmstate')}" != "armed") {
                        if (locks) {
                            for (lock in locks) {
                                if ("${lock.latestValue('lock')}" == "unlocked") {
                                    sendMessage("$lock is Unlocked :(")
                                }
                            }
                        }
                        if (notifyarmed == "Yes") {
                            log.debug "Notify when alarm is Yes and the Alarm is going off"
                            sendMessage("Alarm is Armed")
                        }
                        if ("${params.partitionmode}" == '0') { //away mode (i.e. not at home)
                            if (lightsoff) {
                                lightsoff?.off()
                            }
                            if (thermostataway == "Yes") {
                                if (thermostats) {
                                    for (thermostat in thermostats) {
                                        thermostat.away()
                                    }
                                }
                            }
                            if (sonos) {
                                for (sono in sonos) {
                                    sono.off()
                                }
                            }
                            if (awayMode) {
                                setLocationMode(awayMode)
                            }
                        }
                        if ("${params.partitionmode}" == '3' || "${params.partitionmode}" == '2') { //armed w/zero entry delay
                            if (nightMode) {
                                setLocationMode(nightMode)
                            }
                        }
                    }
                }
                dscthing.dscCommand("${opts[1]}","${params.partitionmode}")
            }
        }
    }
}

private updateZoneDevices(zonedevices,zonenum,zonestatus) {
    log.debug "zonedevices: $zonedevices - ${zonenum} is ${zonestatus}"
    def zonedevice = zonedevices.find { it.deviceNetworkId == "${zonenum}" }
    if (!zonedevice) {

    } else {
        log.debug "Was True... Zone Device: $zonedevice.displayName at $zonedevice.deviceNetworkId is ${zonestatus}"
        //Was True... Zone Device: Front Door Sensor at zone1 is closed
        if ("${zonedevice.latestValue("contact")}" != "${zonestatus}") {
            zonedevice.zone("${zonestatus}")
            def lanaddress = "${settings.xbmcserver}:${settings.xbmcport}"
            def deviceNetworkId = "1234"
            def json = new JsonBuilder()
            def messagetitle = "$zonedevice.displayName".replaceAll(' ','%20')
            log.debug "$messagetitle"
            json.call("jsonrpc":"2.0","method":"GUI.ShowNotification","params":[title: "$messagetitle",message: "${zonestatus}"],"id":1)
            def xbmcmessage = "/jsonrpc?request="+json.toString()
            //sendHubCommand(new physicalgraph.device.HubAction("""GET / HTTP/1.1\r\nHOST: $lanaddress\r\n\r\n""", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}"))
            def result = new physicalgraph.device.HubAction("""GET $xbmcmessage HTTP/1.1\r\nHOST: $lanaddress\r\n\r\n""", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}")
            sendHubCommand(result)
        }

    }
}

private sendMessage(msg) {
    def newMsg = "Alarm Notification: $msg"
    if (phone1) {
        sendSms(phone1, newMsg)
    }
    if (sendPush == "Yes") {
        sendPush(newMsg)
    }
}