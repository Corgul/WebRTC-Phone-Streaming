// Goce - there will be people in the audiance not fimilar with NodeJS, what is "os" for?
// Goce - I think most of these can and should be const 
var os = require("os");
// Goce - Same question as the one above
var nodeStatic = require("node-static");
// Goce - this one is easy, but we can add explination 
var http = require("http");
// Goce - what is this for?
var fs = require("fs");

var fileServer = new nodeStatic.Server();
//Goce -  whart is process.env.PORT for?
const port = process.env.PORT || "5000";

// Setup HTTP server
var app = http
  .createServer((req, res) => {
    fileServer.serve(req, res);
  })
  .listen(port);

// Setup Socket
var socketIO = require("socket.io")(app);
var io = socketIO.listen(app);

/** 
 *  Goce - add this comment from documentation
 * Notice that I initialize a new instance of socket.io by passing the server (the HTTP server) object. 
 * Then I listen on the connection event for incoming sockets and log it to the console.  - Obviosly we don't just log
 **/

io.sockets.on("connection", (socket) => {
  
  // Goce - why are we emitting here?
  function log() {
    var array = ["Message from server:"];
    array.push.apply(array, arguments);
    socket.emit("log", array);
  }

  socket.on("message", (message) => {
    // Goce - I am not sure why we have this logs that do socket.emit("log", array);
    log("Client said: ", message);
    // Goce - will this emit to client B the message that client A sent? What kind of message will this be?
    socket.broadcast.emit("message", message);
  });

  // Goce - I am a JavaScript noob but shouldn't constants such as "create or join" go behind a const var
  socket.on("create or join", (room) => {
    //Goce - I think it would be better if we split this, wouldn't it? socket.on("create") and socker.om("join")
    // was there a resaon why you did it this way
    log("Received request to create or join room " + room);
    
    //Goce - io.sockets.adapter.rooms.get(room); I understsnd what this does buty not sure how
    var clientsInRoom = io.sockets.adapter.rooms.get(room);
    var numClients = clientsInRoom ? clientsInRoom.size : 0;

    log("Room " + room + " now has " + numClients + " client(s)");

    //Goce - you will have to explain this to me, not quite sure I understand what is going on here
    if (numClients === 0) {
      socket.join(room);
      log("Client ID " + socket.id + " created room " + room);
      socket.emit("created", room, socket.id);
    } else if (numClients === 1) {
      log("Client ID " + socket.id + " joined room " + room);
      socket.to(room).emit("peer joined", room);
      socket.join(room);
      socket.emit("joined", room, socket.id);
      io.in(room).emit("ready");
    } else {
      // max two clients
      socket.emit("full", room);
    }
  });

  /** Goce 
   * I am not quite sure I understand what this is for
   * We can replace function () with => ()
   **/
  socket.on("ipaddr", function () {
    //Goce  - this gives us a dictionary where each value is a list of NetworkInterfaceInfo
    var ifaces = os.networkInterfaces();
    for (var dev in ifaces) {
      ifaces[dev].forEach( (details) => {
        // Goce  - can you explain this, for each NetworkInterfaceInfo 
        //  we are emitting the IP address as long as we are using IPv4 and the IP address is not 127.0.0.1? Why?
        if (details.family === "IPv4" && details.address !== "127.0.0.1") {
          socket.emit("ipaddr", details.address);
        }
      });
    }
  });

  //Goce - Go we need this?
  socket.on("bye", function () {
    console.log("received bye");
  });
});
