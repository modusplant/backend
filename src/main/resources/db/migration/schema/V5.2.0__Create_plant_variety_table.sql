CREATE TABLE public.plant_variety (
    variety_code         VARCHAR(12) PRIMARY KEY,
    variety_name         VARCHAR(24) NOT NULL UNIQUE,
    major_category_name  VARCHAR(24) NOT NULL,
    middle_category_code VARCHAR(12) NOT NULL,
    middle_category_name VARCHAR(24) NOT NULL
);
