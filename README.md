# Source: https://www.curseforge.com/minecraft/mc-mods/ipchat

WHAT IS IT?

IPChat provides UDP-based IPC (Inter-Process Communication) between Minecraft and your own program: it enables developers to receive incoming chat messages (both user and server-generated), send messages(as if the user typed them), issue commands (as the user) show client-only echo messages and (coming soon) registering your own commands. Currently the mod focuses on client side.

WHAT CAN YOU DO?

IPChat makes integrating existing applications with Minecraft a breeze. No further mods required: everything happens on UDP!

Want to respond to chat messages while you are smelting? Covered. Want to receive data directly in your chat? Covered. Want to troll people? Covered. Want to use your Arduino to control your Minecraft world? Covered. Want to use your Minecraft world to control your Arduino? Covered as well! Just code the proper firmware. Want to tweet something if somebody on the server says "Donald Trump"? You are covered, just make a program that can tweet and hook it to IPChat! Want to be kicked instantly from the server? Just make a program that sends "§" as a chat message and you are covered too! :P

HOW DO I GET STARTED?

    UDP->MinecraftAddress: 127.0.0.1:12345

    Minecraft->UDP Address: 127.0.0.1:54321

    Datagrams format is: <VERB>{§}PARAM

    Encoding: Unicode (UTF-16) Little Endian (without BOM)

    To send chat messages use "CHT" as VERB and your message as PARAM

    To receive chat you need to listen on 127.0.0.1:54321

    Relayed chat messages have "MEX" as VERB and the message as PARAM: <MEX>{§}Somebody's message.

        For example to send "Hello!" i would need to send "<CHT>{§}Hello!" over UDP to port 12345 of my PC.

Note: IPChat sends a <HELO> datagram during startup and every time the client has connected to a server. There is a <SVR> datagram message planned for specifying server data.

FUTURE FEATURES (unordered)

    Custom client-side commands registration and callback system.
    Add support for FML event realying
    Add support for FML mod injection
    Implementing Forge settings GUI to change port numbers & manage things easily.
    Allow control over the Internet
    <SVR> message upon connection to a server
    Verb to get a list of the players.
    Decide if switch to JSON/XML/something better than a plain text protocol or keep the thing simple so that it is easier to consume
    Protocol negotiation with an <INFO> packet that holds informations
    Server support
    .Net API (almost complete)
    Java API
    Do a LAN test to check that I setup the socket correctly

USELESS FEATURES ADDED RANDOMLY AND THEN DISCARDED/UNDOCUMENTED

    /logpy shows the current pitch and yaw values, that can be changed with /setpy <pitch> <yaw>
    /rclick - simulates a right click
    Fix for other mods that can't use client side commands

BUGS

IPChat is still in its infancy so any bug you find and any suggestion or remark you make in the comments will be appreciated.

    Connection is unstable in some situations.

# Decompiled with Vineflower: 
`java -jar vineflower-1.11.1.jar -dgs=1 UdpChatSocket-1.0.jar C:\Users\folder\output`


