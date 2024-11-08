var os = require("os");
var nodeStatic = require("node-static");
var http = require("http");
var fs = require("fs");

var fileServer = new nodeStatic.Server();
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

io.sockets.on("connection", (socket) => {
  function log() {
    var array = ["Message from server:"];
    array.push.apply(array, arguments);
    socket.emit("log", array);
  }

  socket.on("message", (message) => {
    log("Client said: ", message);
    socket.broadcast.emit("message", message);
  });

  socket.on("create or join", (room) => {
    log("Received request to create or join room " + room);

    var clientsInRoom = io.sockets.adapter.rooms.get(room);
    var numClients = clientsInRoom ? clientsInRoom.size : 0;

    log("Room " + room + " now has " + numClients + " client(s)");

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

  socket.on("ipaddr", function () {
    var ifaces = os.networkInterfaces();
    for (var dev in ifaces) {
      ifaces[dev].forEach(function (details) {
        if (details.family === "IPv4" && details.address !== "127.0.0.1") {
          socket.emit("ipaddr", details.address);
        }
      });
    }
  });

  socket.on("bye", function () {
    console.log("received bye");
  });
});
