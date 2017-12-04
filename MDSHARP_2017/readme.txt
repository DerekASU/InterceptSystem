MDS Simulator Readme
Last Updated: 10/6/2017
POC if any bugs or unexpected behaviors are found: conner.lines@gd-ms.com (feel free to reach out to me if something goes wrong)

Notable Features in the 2017 MDS Simulator:
1. Scenarios: Scenarios are the newest feature in the MDS Simulator and will hopefully make your life significantly easier as you progress through the project. A scenario is comprised of information on the missiles within the MDS 
Simulator before a simulation is started. Using the features described below you should be able to create, modify, and delete scenarios as you see fit for testing.

2. Interceptor Options Panel:
	- Add Interceptor: Pressing this button will create a new window in which you can select what class of interceptor you'd like to create and enter its X, Y, and Z coordinates. The default dropdown option is "Random". If you'd like
	  to create a new interceptor but don't care what class or where its located, leave Random selected and hit Save to create a random interceptor.
	- Remove interceptor: Pressing this button will remove the currently selected interceptor from the list at the top of the MDS Simulator. You can select what interceptor to remove by simply clicking the desired interceptor in
          the Interceptor Safety, Interceptor Control, or Interceptor Tracking lists.
	- Customize Interceptor: Pressing this button will bring up a new window similar to the Add Interceptor window, but with the information of the selected interceptor present in the input fields. 

2. Threat Options Panel: These buttons function similar to their interceptor counterparts, but instead for threats.
	- Add Threat, Customize Threat: These buttons will create a window with three additional fields in it for the Threat's Velocity.

3. Scenario Options:
	- Save Scenario: Pressing this button will create a new window asking for a scenario name. Clicking save will take the missiles' current position and velocities (if a threat) and store them in a file with the name specified.
	- Load Scenario: Before pressing this button, you will need to select a Scenario within the Scenarios Panel to the right. After selecting the desired scenario and pressing the Load Scenario button, the simulator will load the 
          scenario and be ready to run.
	- Delete Scenario: Before pressing this button, you will need to select a Scenario within the Scenarios Panel to the right. After selecting the desired scenario and pressing the Delete Scenario button, the selected scenario will
	  be deleted from your file system and disappear from the Scenarios Panel.

4. Scenarios: Lists the available Scenarios
	- currentScenario.json - This scenario will be populated and overwritten every time the MDS Simulator is closed and opened. If you wish to revisit your current currentScenario, please save it under a different name. 
	- Random - This scenario when loaded will generate a new random scenario with seven (patent pending can't remember if its supposed to be 7 or 5) interceptors and five (trademarked for the same reason) threats. Use this functionality
	  if you need a new test case and don't want to restart the simulator. This scenario does not have an associated json file.
	- Sample Scenarios: Currently there are three provided sample scenarios: 7As, 7Bs, 7Cs. These are scenarios where all seven interceptors share the same interceptor type and can be used to isolate issues with specific types. More
	  may be provided at a later date and not be added to this because I'll probably forget let's be real.

5. Reset: Pressing the Reset button while the simulator has been started will immediately stop the missiles and subsystems and then load the currentScenario.json scenario. Depending on how you design your project, you may or may not have
to close your project and restart it (past projects will definitely have to, future ones depend on a conscious design choice to add a project side reset button). 

Other things to note:
	- Interceptor Options, Threat Options, and Scenario Options will all be disabled when the simulation has been started. Stopping or Resetting the simulation will reenable them. 
	- There were a fair amount of changes to the structural organization of this project. While testing was done, I only had access to one MDSCSS project to work with and limited time. IF YOU FIND A PROBLEM, do not hesitate to email 
	  me with a description of the problem and, if possible (it really helps), the steps taken to create this problem. 
	- Suggestions: Also shoot those my way. If something being added to the simulator would make testing easier, let me know. I may not (99% won't) add them before your A-Course is over, but having someone collect feedback for the next
	  person to make adjustments is never a bad thing.
	- You do not need to create scenarios through the scenario UI if you find it tedious. All scenarios are saved in a folder named "Scenarios" that is created in the same directory you run the executable (assuming you don't copy over
	  the one provided with the samples). All scenarios are saved in .json files, in json format. If you are unfamiliar with json, it's really easy and fast to learn. There are also a number of syntax validators online if you aren't
	  sure what you're doing is correct. Check out the sample scenarios for an idea on how to format them.
	- If you make your own json file scenarios, mess something up, and email me about it, I'll not like you. If you do encounter issues after doing this, be sure to run the content of your file through an online validator and double
	  check the sample scenarios' format.
	- This project will take longer than you think guaranteed. Start early. 

