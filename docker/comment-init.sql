CREATE TABLE IF NOT EXISTS public.comment
(
    id           varchar(50)  NOT NULL,
    article_id    varchar(100) NOT NULL,
    content      text         NOT NULL,
    author       varchar(100) NOT NULL,
    create_time   timestamp    NOT NULL,
    modified_time timestamp    NOT NULL,

    CONSTRAINT comment_pkey PRIMARY KEY (id)
)
TABLESPACE pg_default;

ALTER TABLE public.comment OWNER TO postgres;