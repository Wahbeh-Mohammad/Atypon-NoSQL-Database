import React, { useState } from 'react';
import { Box, Button } from "@mui/material";
import SchemaIcon from '@mui/icons-material/Schema';
import SchemaDetails from './SchemaDetails';
import "../styles/AllSchemas.css";

const Schemas = (props) => {
    const { schemas, schemaNames, databaseName } = props;
    const [schemaName, setSchemaName] = useState("");
    const [schemaConnectedTo, setSchemaConnectedTo] = useState({});
    const [connectedToSchema, setConnectedToSchema] = useState(false);

    const handleChangeSchema = (name) => {
        setSchemaName(name);
        setSchemaConnectedTo(schemas[name]);
        setConnectedToSchema(true);
    }

    return (
        <Box className="all-schemas">
            <Box className="schema-controls align-center" sx={{borderBottom:1, borderColor:"divider"}}>
                <SchemaIcon color="primary"/>
                { schemaNames && schemaNames.map((schemaName, idx) => {
                    return ( 
                        <Button 
                            key={idx}
                            value={schemaName} 
                            size="medium"
                            onClick={e => handleChangeSchema(e.target.value)}> {schemaName} </Button>
                    )
                }) }
            </Box>
            { connectedToSchema && <SchemaDetails schemaName={schemaName} schema={schemaConnectedTo} databaseName={databaseName} />}
        </Box>
    );
}
 
export default Schemas;