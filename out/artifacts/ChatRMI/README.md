# How to use ChatRMI as a User ?

1. You must edit the `runMain.cmd` file to adapt the **-DIP_Chat** property :
	* This the IP on which you will be shared on the network through RMI for other users to connect to you.
	* Therefore, this is also the IP you will add into the Registry (for other users to see).
	
2. You must edit the `runMain.cmd` to adapt the **-DIP_Registry** property :
	* This the IP on which you will connect to find other users and start chatting with them.

# How to use ChatRMI as a Registry ?
	
You must only edit the `runRegistry.cmd` file to adapt the **-DIP_Registry** property :
	* This is the IP on which ALL the users will connect to share their IP and find other users.
	
# Is it possible to run a User and a Registry on the same computer ?

**Yes it is !**

In such a case, the **-DIP_Chat** and **-DIP_Registry** properties of the `runMain.cmd` file as well as the **-DIP_Registry** property of the `runRegistry.cmd` will ALL THREE share the same IP value !