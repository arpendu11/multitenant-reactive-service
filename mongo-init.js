db.createUser(
    {
        user: "spring",
        pwd: "password",
        roles: [
            { role: "root", db: "admin" },
            { role: "readWrite", db: "master" },
            { role: "read", db: "master" }
        ]
    }
);