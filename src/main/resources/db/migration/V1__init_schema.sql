CREATE TABLE clients (
                         id VARCHAR(255) PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         dtype VARCHAR(50) NOT NULL,
                         parts_discount NUMERIC(5, 2),
                         labor_discount NUMERIC(5, 2)
);

CREATE TABLE cars (
                      vin VARCHAR(255) PRIMARY KEY,
                      make VARCHAR(255) NOT NULL,
                      model VARCHAR(255) NOT NULL,
                      car_year INT NOT NULL,
                      client_id VARCHAR(255) NOT NULL,
                      CONSTRAINT fk_car_client FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE TABLE mechanics (
                           id VARCHAR(255) PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           specialization VARCHAR(50) NOT NULL,
                           is_busy BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE repair_orders (
                               id VARCHAR(255) PRIMARY KEY,
                               car_vin VARCHAR(255) NOT NULL,
                               mechanic_id VARCHAR(255),
                               status VARCHAR(50) NOT NULL,
                               CONSTRAINT fk_order_car FOREIGN KEY (car_vin) REFERENCES cars(vin),
                               CONSTRAINT fk_order_mechanic FOREIGN KEY (mechanic_id) REFERENCES mechanics(id)
);

CREATE TABLE works (
                       id BIGSERIAL PRIMARY KEY,
                       order_id VARCHAR(255) NOT NULL,
                       description TEXT NOT NULL,
                       parts_cost NUMERIC(19, 2) NOT NULL,
                       labor_cost NUMERIC(19, 2) NOT NULL,
                       is_done BOOLEAN NOT NULL DEFAULT FALSE,
                       CONSTRAINT fk_work_order FOREIGN KEY (order_id) REFERENCES repair_orders(id) ON DELETE CASCADE
);