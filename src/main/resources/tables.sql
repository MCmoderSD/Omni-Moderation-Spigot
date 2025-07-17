CREATE TABLE IF NOT EXISTS Content (
    id          VARCHAR(36)     PRIMARY KEY,
    content     VARCHAR(256)    UNIQUE NOT NULL,
    flagged     INTEGER         CHECK(flagged IN (-1, 0, 1)) NOT NULL DEFAULT -1
);

CREATE TABLE IF NOT EXISTS Rating (
    id          VARCHAR(36)     PRIMARY KEY,
    flagged     BOOLEAN         NOT NULL DEFAULT FALSE,
    rating      BLOB            NOT NULL,
    FOREIGN KEY (id) REFERENCES Content(id)
);

CREATE TABLE IF NOT EXISTS Category (
    id                      VARCHAR(36) PRIMARY KEY,
    harassment              BOOLEAN NOT NULL DEFAULT FALSE,
    harassmentThreatening   BOOLEAN NOT NULL DEFAULT FALSE,
    hate                    BOOLEAN NOT NULL DEFAULT FALSE,
    hateThreatening         BOOLEAN NOT NULL DEFAULT FALSE,
    illicit                 BOOLEAN NOT NULL DEFAULT FALSE,
    illicitViolent          BOOLEAN NOT NULL DEFAULT FALSE,
    selfHarm                BOOLEAN NOT NULL DEFAULT FALSE,
    selfHarmIntent          BOOLEAN NOT NULL DEFAULT FALSE,
    selfHarmInstructions    BOOLEAN NOT NULL DEFAULT FALSE,
    sexual                  BOOLEAN NOT NULL DEFAULT FALSE,
    sexualMinors            BOOLEAN NOT NULL DEFAULT FALSE,
    violence                BOOLEAN NOT NULL DEFAULT FALSE,
    violenceGraphic         BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id) REFERENCES Rating(id)
);

CREATE TABLE IF NOT EXISTS Score (
    id                      VARCHAR(36) PRIMARY KEY,
    harassment              DOUBLE NOT NULL DEFAULT 0 CHECK (harassment >= 0 AND harassment <= 1),
    harassmentThreatening   DOUBLE NOT NULL DEFAULT 0 CHECK (harassmentThreatening >= 0 AND harassmentThreatening <= 1),
    hate                    DOUBLE NOT NULL DEFAULT 0 CHECK (hate >= 0 AND hate <= 1),
    hateThreatening         DOUBLE NOT NULL DEFAULT 0 CHECK (hateThreatening >= 0 AND hate <= 1),
    illicit                 DOUBLE NOT NULL DEFAULT 0 CHECK (illicit >= 0 AND illicit <= 1),
    illicitViolent          DOUBLE NOT NULL DEFAULT 0 CHECK (illicitViolent >= 0 AND illicitViolent <= 1),
    selfHarm                DOUBLE NOT NULL DEFAULT 0 CHECK (selfHarm >= 0 AND selfHarm <= 1),
    selfHarmIntent          DOUBLE NOT NULL DEFAULT 0 CHECK (selfHarmIntent >= 0 AND selfHarmIntent <= 1),
    selfHarmInstructions    DOUBLE NOT NULL DEFAULT 0 CHECK (selfHarmInstructions >= 0 AND selfHarmInstructions <= 1),
    sexual                  DOUBLE NOT NULL DEFAULT 0 CHECK (sexual >= 0 AND sexual <= 1),
    sexualMinors            DOUBLE NOT NULL DEFAULT 0 CHECK (sexualMinors >= 0 AND sexualMinors <= 1),
    violence                DOUBLE NOT NULL DEFAULT 0 CHECK (violence >= 0 AND violence <= 1),
    violenceGraphic         DOUBLE NOT NULL DEFAULT 0 CHECK (violenceGraphic >= 0 AND violenceGraphic <= 1),
    FOREIGN KEY (id) REFERENCES Rating(id)
);

CREATE TABLE IF NOT EXISTS User (
    uuid            VARCHAR(36) PRIMARY KEY,
    name            VARCHAR(16) NOT NULL,
    displayName     VARCHAR(16) NOT NULL
);

CREATE TABLE IF NOT EXISTS Message (
    id          VARCHAR(36)     PRIMARY KEY,
    user        VARCHAR(36)     NOT NULL,
    content     VARCHAR(36)     NOT NULL,
    timestamp   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user)      REFERENCES User(uuid),
    FOREIGN KEY (content)   REFERENCES Content(id)
);