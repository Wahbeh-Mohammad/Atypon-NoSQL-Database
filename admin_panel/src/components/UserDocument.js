import React, { useState } from 'react';
import Editor from '@monaco-editor/react';
import { Paper, Box, Button, Typography } from "@mui/material";
import InfoIcon from '@mui/icons-material/Info';
import Cookies from 'universal-cookie';

const UserDocument = (props) => {
    const { userData } = props;
    const cookie = new Cookies();
    const [token] = useState(cookie.get("token"));
    const [user, setUser] = useState(JSON.stringify(userData, null, 2));
    const [info, setInfo] = useState("");
    const [infoColor, setInfoColor] = useState("");

    const handleDelete = () => {
        fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/admin/user/delete`, {
            method:"DELETE",
            headers : {
                "content-type": "application/json",
                "authorization": token
            },
            body: user
        })
        .then(response => response.json())
        .then(data => {
            if(data.status === "GOOD") {
                setInfo(data.message);
                setInfoColor("green");
                setTimeout(()=>{setUser(null)}, 750);
            } else {
                setInfo(data.message);
                setInfoColor("red");
            }
        })
    }

    return (
        <>
            {!user && <></>}
            {user && 
                <Paper className="full-document padding">
                    <Box className="data">
                        <Editor 
                            options={{
                                minimap: {
                                    enabled: false
                                },
                                scrollbar: false,
                                scrollBeyondLastLine: false
                            }}
                            height="15rem"
                            width="100%"
                            language={"json"}
                            defaultValue={user}
                            onChange={(e) => setUser(e)} />
                    </Box>
                    <Box className="document-operations">
                        <Box className="flex-row-gap">
                            <Button variant="contained" color="error" onClick={handleDelete}> Delete User </Button>
                        </Box>
                        { info && <Typography color={infoColor} className="flex-row-gap align-center"> <InfoIcon /> {info} </Typography>}
                    </Box>
                </Paper>
            } 
        </>
    );
}
 
export default UserDocument;