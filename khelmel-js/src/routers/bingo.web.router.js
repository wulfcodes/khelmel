const express = require('express');
const router = express.Router();
const BingoController = require('../controllers/bingo.controller');

router.get('/', BingoController.index);

module.exports = router;
