CREATE TABLE IF NOT EXISTS public.login_failures
(
    userId           varchar(50)  NOT NULL,
    failureCount     Integer      NOT NULL,
    lastFailureTime  timestamp    NOT NULL,

    CONSTRAINT login_failures_pkey PRIMARY KEY (userId)
)
TABLESPACE pg_default;

ALTER TABLE public.login_failures OWNER TO postgres;
