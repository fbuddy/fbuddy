--
-- Copyright © 2016-2024 The BARC Authors
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- USER MANAGEMENT UPDATE START

ALTER TABLE tb_user ADD COLUMN IF NOT EXISTS profile_type VARCHAR(50);
ALTER TABLE tb_user ADD COLUMN IF NOT EXISTS type VARCHAR(50);
ALTER TABLE tb_user ADD COLUMN IF NOT EXISTS company VARCHAR(50);
ALTER TABLE tb_user ADD COLUMN IF NOT EXISTS department VARCHAR(50);
ALTER TABLE tb_user ADD COLUMN IF NOT EXISTS designation VARCHAR(50);
ALTER TABLE tb_user ADD COLUMN IF NOT EXISTS serial_number INTEGER;
ALTER TABLE tb_user ADD COLUMN IF NOT EXISTS user_name character varying(50) COLLATE pg_catalog."default";
ALTER TABLE tb_user ADD COLUMN IF NOT EXISTS profile_status character varying(50) COLLATE pg_catalog."default";
