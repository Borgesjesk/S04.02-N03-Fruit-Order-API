db = db.getSiblingDB(process.env.MONGO_INITDB_DATABASE);

db.createUser({
    user: process.env.APP_DB_USER,
    pwd: process.env.APP_DB_PASS,
    roles: [
        {
            role: 'readWrite',
            db: process.env.MONGO_INITDB_DATABASE
        }
    ]
});