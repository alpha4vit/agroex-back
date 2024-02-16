CREATE TABLE IF NOT EXISTS public.colors(
    id BIGSERIAL,
    bg_color_hex varchar(10),
    text_color_hex varchar(10)
);

INSERT INTO public.colors(bg_color_hex, text_color_hex) VALUES
                                                                ('#9FA5C8', '#FFFFFF'),
                                                                ('#E3D7CF', '#333333'),
                                                                ('#FCAE4A', '#FFFFFF'),
                                                                ('#3FC380', '#FFFFFF'),
                                                                ('#ED553B', '#FFFFFF'),
                                                                ('#5E4FA2', '#FFFFFF'),
                                                                ('#34A853', '#FFFFFF'),
                                                                ('#FF6D00', '#FFFFFF'),
                                                                ('#F5A758', '#FFFFFF'),
                                                                ('#FFC857', '#FFFFFF'),
                                                                ('#499FA4', '#FFFFFF'),
                                                                ('#ADD8E6', '#333333');

DO $$
    DECLARE
        min_color_index INTEGER;
        max_color_index INTEGER;
    BEGIN
        SELECT MIN(id), MAX(id) INTO min_color_index, max_color_index FROM colors;
        EXECUTE 'CREATE SEQUENCE cycle_sequence START WITH ' || min_color_index || ' INCREMENT BY 1 MINVALUE ' || min_color_index || ' MAXVALUE ' || max_color_index || ' CYCLE';
    END $$;

ALTER TABLE public.tags ADD COLUMN color_id INT;
ALTER TABLE public.tags ALTER COLUMN color_id SET DEFAULT nextval('cycle_sequence');

TRUNCATE public.tags cascade;

INSERT INTO public.tags (title, color_id) VALUES ('Fresh', 4),
                                                 ('Organic', 7),
                                                 ('Hot sale', 5),
                                                 ('Best price', 3);





