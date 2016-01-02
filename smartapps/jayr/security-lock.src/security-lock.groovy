/**
 *  Security Lock
 *
 *  Copyright 2015 Jason Rhykerd
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Security Lock",
    namespace: "jayr",
    author: "Jason Rhykerd",
    description: "Lock the door staying in sync of a virtual switch to use with Alexa",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
		section("When this switch is turned on") {
		input "switch1", "capability.switch", multiple: false, required: true
		}
	section("Lock/UnLock this lock...") {
		input "lock1", "capability.lock", multiple: false, required: true
		}
    section("Only if these people are home...") {
        input "people1", "capability.presenceSensor", title: "Who?", required: true, multiple: true
		}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
    subscribe(switch1, "switch", switchHandler)
    subscribe(lock1, "lock", lockHandler)
    subscribe(people1, "presence", myHandler)
}

def myHandler(evt) {
// not doing anything with presence events...
}

def switchHandler(evt) {
	if (("on" == evt.value)){
      log.debug evt.value
  	  log.debug "Locking lock: $lock1"
	  if ("unlocked" == lock1.currentValue("lock")){
        lock1.lock()
      }
    }else if (("off" == evt.value) && someoneHome()){
      log.debug evt.value
  	  log.debug "Unlocking lock: $lock1"
	  lock1.unlock()
    }else {
      //Turn the switch back on because we did not unlock it - lock is still locked
      switch1.on()
      log.debug "Current Lock status is:" 
      log.debug lock1.currentValue("lock")
    }
}

def lockHandler(evt) {
	if (("locked" == evt.value)){
      log.debug evt.value
  	  log.debug "Turning on switch: $switch1"
	  switch1.on()
    } else if ("unlocked" == evt.value){
      log.debug evt.value
	  log.debug "Turning off switch: $switch1"
   	  switch1.off()
    }
}

private someoneHome(){
        def whoIsHome = people1.findAll{it.currentPresence == "present"}
        if(whoIsHome.size() > 0){
        	log.debug whoIsHome.size()
        	return true
        }
        log.debug whoIsHome.size()
        return false
    }