-- Oracle 12c ou superior
CREATE TABLE news (
    id           NUMBER GENERATED ALWAYS AS IDENTITY,
    title        VARCHAR2(255)  NOT NULL,
    subtitle     VARCHAR2(255),
    content      CLOB           NOT NULL,
    redirection  VARCHAR2(2048),
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT pk_news PRIMARY KEY (id)
);

CREATE INDEX idx_news_created_at ON news (created_at DESC);

-- Oracle 11g: substitua a coluna id por "id NUMBER NOT NULL" e execute também:
-- CREATE SEQUENCE seq_news START WITH 1 INCREMENT BY 1 NOCACHE;
-- CREATE OR REPLACE TRIGGER trg_news_id
-- BEFORE INSERT ON news
-- FOR EACH ROW
-- WHEN (NEW.id IS NULL)
-- BEGIN
--     :NEW.id := seq_news.NEXTVAL;
-- END;
-- /
