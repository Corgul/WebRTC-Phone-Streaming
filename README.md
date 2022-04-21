# WebRTC Phone Streaming App

## Showcase

This app is meant to be a simple demo on how to stream from one phone to another phone. In the app there is a sender and a receiver. Both have sides have the ability to join or create a new socket room in whatever order.

To stream:
1. On one phone enter the name of the room you want to join. This will create a new socket.io room
2. On the other phone, enter the exact same name of the room to join.
3. If all other parts of the setup are complete they should both connect successfully, the sender will show it's own camera view and the receiver should show the sender's camera view.



## Getting Started

### Setting up the signaling server on Heroku

The first step of the app should be setting up the signaling server. This needs to be setup for any of the room creation or communication logic to work successfully.

First run this comment in the Node-Signaling-Server directory.
```
npm install
```

Next download heroku and run
```
$ git add .
$ git commit -m "Initial commit"
$ heroku login
Enter your Heroku credentials.
...
heroku create
Creating arcane-lowlands-8408... done, stack is cedar
http://arcane-lowlands-8408.herokuapp.com/ | git@heroku.com:arcane-lowlands-8408.git
Git remote heroku added
$ git push heroku main
```

The Node signaling server should now be running on your heroku server.

To verify it's up and running you can run
```
curl "https://heroku-url.herokuapp.com/socket.io/?EIO=4&transport=polling"
```
and it should return
```
0{"sid":"Lbo5JLzTotvW3g2LAAAA","upgrades":["websocket"],"pingInterval":25000,"pingTimeout":5000}
```

### Setting up Xirsys

We went with Xirsys, a free cloud TURN server platform. You can also go with something like hosting your own CoTURN server on AWS. To get started with Xirsys, make an account [here](https://xirsys.com/). Create a project on there. Next find these parameters on the dashboard and take note of them, we will put them in the Android project.


### Running the app

Now we can finally setup the Android project. First change the signaling server URL to your heroku app's URL.

Change the return statement in `SignalingModule.provideModule()` to return your url.

Next add called `apikey.properties` in the app module and add this to the conents:
```
XIRSYS_IDENT="{Your xirsys Ident field}"
XIRSYS_SECRET="{Your xirsys secret}"
XIRSYS_CHANNEL="{Your xirsys channel}"
```

With the heroku signaling server, the Xirsys TURN server, and your app successfully running on two phones, you should now be able to stream from one phone to another if both phones join the same room name.
