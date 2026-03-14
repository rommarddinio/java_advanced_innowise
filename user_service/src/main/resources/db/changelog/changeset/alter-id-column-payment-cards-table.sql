CREATE SEQUENCE payment_cards_id_seq;
ALTER TABLE payment_cards
ALTER COLUMN id SET DEFAULT nextval('payment_cards_id_seq')

