use crate::token;

pub fn tokenize(input: &str) -> Vec<token::Token> {
    let mut tokens: Vec<token::Token> = vec![];

    let mut i: usize = 0;
    while i < input.len() {
        let ch: char = input.chars().nth(i).unwrap();

        if ch.is_ascii_digit() || ch == '-' || ch == '+' {
            tokens.push(token::Token::IntegerLiteral(parse_number(input, &mut i)));
        } else {
            panic!("Invalid character: {}", input.chars().nth(i).unwrap());
        }
    }

    tokens
}

fn parse_number(input: &str, i: &mut usize) -> i64 {
    let ch: char = input.chars().nth(*i).unwrap();
    let mut x: i64 = 0;
    let mut is_negative: bool = false;

    if ch == '+' || ch == '-' {
        is_negative = ch == '-';
        *i += 1;
    }

    while *i < input.len() && input.chars().nth(*i).unwrap().is_ascii_digit() {
        x = 10 * x + input.chars().nth(*i).unwrap().to_digit(10).unwrap() as i64;
        *i += 1;
    }

    if is_negative {
        -x
    } else {
        x
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use test_case::test_case;

    #[test_case("0", 0 ; "zero")]
    #[test_case("1", 1 ; "one")]
    #[test_case("+1", 1 ; "plus_1")]
    #[test_case("-1", -1 ; "minus_1")]
    fn number_parsing(input: &str, output: i64) {
        assert_eq!(parse_number(input, &mut 0), output);
    }
}
