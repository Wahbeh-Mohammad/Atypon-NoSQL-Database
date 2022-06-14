const express = require("express");
const cors = require("cors");
const { exec } = require("child_process");
const app = express();

app.use(express.json());
app.use(cors());

const NODE_DISCOVERY_COMMAND = `docker ps -a --filter "ancestor=openjdk:8" | grep source_code_readcontroller | awk '{ print $1 }' | 
xargs docker inspect -f '{"hostname":"{{.Config.Hostname}}","containerName":"{{.Name}}","state":"{{.State.Status}}","ip":"{{range.NetworkSettings.Networks}}{{.IPAddress}}" {{end}}}'`

app.get('/cluster/nodes', (req, res) => {
    exec(NODE_DISCOVERY_COMMAND, (err, stdout, stderr) => {
        if(err) {
            console.log(err);
            return res.send({"message":"Failed to get container information", "status":"BAD"});
        }

        var allContainersInformation = [];
        const containersInfo = stdout.split("\n");
        containersInfo.forEach(containerInfo => {
            if(containerInfo) {
                allContainersInformation.push(JSON.parse(containerInfo));
            }
        });

        if(allContainersInformation)
            return res.send({"message":"Discovered", content: allContainersInformation, "status":"GOOD"});
        else
            return res.send({"message":"Failed to get container information", "status":"BAD"});
    });
});

app.post('/cluster/scale/:nodes', (req,res) => {
    const { nodes } = req.params;
    const SCALE_COMMAND = `cd ../ && docker-compose scale readcontroller=${nodes}`
    exec(SCALE_COMMAND, (err, stdout, stderr) => {
        if(err) {
            console.log(err);
            return res.send({"message":"Failed to scale the containers", "status":"BAD"});
        }
        return res.send({"message":"Scaled successfully", "status":"GOOD"});
    });
})



const build = () => {
    app.listen(8002, () => {
        console.log("Server listening on port : 8002");
    });
}

build();