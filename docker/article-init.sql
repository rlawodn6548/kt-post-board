CREATE TABLE IF NOT EXISTS public.article
(
    id           varchar(50)  NOT NULL,
    title        varchar(100) NOT NULL,
    content      text         NOT NULL,
    author       varchar(100) NOT NULL,
    author_id    varchar(50) NOT NULL,
    create_time   timestamp    NOT NULL,
    modified_time timestamp    NOT NULL,

    CONSTRAINT article_pkey PRIMARY KEY (id)
)
TABLESPACE pg_default;

ALTER TABLE public.article OWNER TO postgres;