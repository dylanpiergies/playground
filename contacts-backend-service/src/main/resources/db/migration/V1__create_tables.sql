CREATE TABLE "contact" (
    "id"        SERIAL PRIMARY KEY,
    "title"     VARCHAR(31)  NULL,
    "forenames" VARCHAR(255) NULL,
    "surname"   VARCHAR(255) NULL
);

CREATE TABLE "telephone_number" (
    "id"         SERIAL PRIMARY KEY,
    "number"     VARCHAR(255) NOT NULL,
    "type"       VARCHAR(31)  NULL,
    "contact_id" INTEGER      NOT NULL REFERENCES "contact" ("id") ON DELETE CASCADE
);
