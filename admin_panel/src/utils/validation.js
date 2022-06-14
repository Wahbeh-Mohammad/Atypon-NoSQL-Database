export const validateSchema = (schemaObject) => {
    try {
        const { schemaName, schema } = JSON.parse(schemaObject);
        if(!schemaName) return { valid:false, color:"red", info:"Invalid schema name" };
        if(!schema) return { valid:false, color:"red", info:"Invalid schema" };

        const fields = Object.keys(schema);
        var isValid = true;
        for(var index = 0; index < fields.length; index++){
            var fieldName = fields[index];
            if(fieldName === "" || fieldName === null)
                return { valid:false, color: "red", info :"Schema contains invalid fields" };
            
            var fieldType = schema[fieldName];
            if(fieldType !== "Boolean" && fieldType !== "Integer" && fieldType !== "String" && fieldType !== "Array"){
                isValid = false;
            }
        }
        if(isValid)
            return { valid: true, color:"green", info:"New schema created" };
        else
            return { valid:false, color: "red", info :"Schema contains invalid field types" };
    } catch (e) {
        return {valid: false, color:"red", info:"Invalid json" }
    }
}